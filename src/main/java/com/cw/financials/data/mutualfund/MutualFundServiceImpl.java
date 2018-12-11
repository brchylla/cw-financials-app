package com.cw.financials.data.mutualfund;

import com.cw.financials.data.SiteAccess;
import com.cw.financials.data.SiteAccessService;
import com.cw.util.XHTMLParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.springframework.data.domain.PageRequest;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * Created by ben chylla on 7/23/18.
 * Aggregates a MutualFundRepository for additional searching and downloading capability
 */
@Service
public class MutualFundServiceImpl implements MutualFundService {
    private static final Duration defaultIgnoreTime = Duration.ofHours(1);
    private static final String rootURLMutualFundMetaData = "https://finance.yahoo.com/quote/";
    private static final String categoryValueField = "data-test=\"CATEGORY-value\"";
    private static final String inceptionDateField = "data-test=\"FUND_INCEPTION_DATE-value\"";

    @Autowired
    MutualFundRepository mfRepo;
    @Autowired
    SiteAccessService siteService;

    /**
     * Default constructor that uses autowiring
     */
    public MutualFundServiceImpl() {}

    public MutualFundRepository getRepository() {
        return mfRepo;
    }

    public void setRepository(MutualFundRepository mfRepo) {
        this.mfRepo = mfRepo;
    }

    /**
     * Contructor
     * @param repo  Repository to aggregate
     */
    public MutualFundServiceImpl(MutualFundRepository repo) {
        mfRepo = repo;
    }

    /**
     * Asynchronous calls syncMutualFunds
     * @param url   Site to parse for mutual fund data
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    @Override
    public void syncMutualFundsAsync(URL url, Duration ignorePeriod) throws MalformedURLException,
            ProtocolException, IOException {
        /*SiteAccess sa2 = new SiteAccess(url.toString());
        sa2.setLastUpdated(new Date());*/
        SiteAccess sa2 = new SiteAccess(url.toString(), new Date()); // sets updating time to current time
        siteService.getRepo().save(sa2);
        if (ignorePeriod == null) {
            ignorePeriod = defaultIgnoreTime;
        }
        if ( !siteService.accessedWithin(url.toString(),ignorePeriod) ) {
            Runnable runnable  = new Runnable() {
                @Override
                public void run() {
                    syncMutualFunds(url);
                    sa2.setLastCompleted(new Date()); // sets completion time to current time
                    siteService.getRepo().save(sa2);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    /**
     * Synchronizes mutual funds in the repository with data from external site
     * @param url   External site
     */
    @Override
    public void syncMutualFunds(URL url) {
        int numEntries = 0;
        try {
            URLConnection conn = url.openConnection();
            System.out.println("Synchronizing mutual funds...");
            try (InputStream inputStream = conn.getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                if (line == null) {
                    return;
                }
                line = reader.readLine(); // call readLine twice to iterate past headers
                while( (line != null) && (line.length() > 0) ) {
                    String[] values = line.split("\\|");
                    if ( values.length > 2 ) {
                        String fundSymbol = values[0];
                        String fundName = values[1];
                        String fundFamilyName = values[2];
                        if (mfRepo.findMutualFundByFundSymbol(fundSymbol.toUpperCase()) == null) {
                            MutualFund mf = new MutualFund(fundSymbol, fundName, fundFamilyName);
                            mfRepo.save(mf);
                            numEntries++;
                            System.out.println("Saved into repository " + mf);
                        }
                    }
                    line = reader.readLine();
                }
            }
        } catch (SocketException socketException) {
            // DO nothing. This is expected at end of stream
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("Done saving " + numEntries + " mutual funds to repository");
        }
    }

    /**
     * Search for list of mutual funds by symbol, page number, and max entries
     * @param fundSymbol    Symbol with contain logic
     * @param max           Maximum entries to return in list
     * @return              List of mutual funds that contain symbol subject to max length
     */
    @Override
    public List<MutualFund> findMutualFundsByFundSymbolContains(String fundSymbol, int pageNo, int max) {
        return mfRepo.findMutualFundsByFundSymbolContains(fundSymbol.toUpperCase(), new PageRequest(pageNo, max));
    }

    /**
     * Adds mutual fund to repository if no mutual fund with matching symbol exists there
     * If mutual fund with matching symbol already exists in repository, the fund is updated w/ the new data
     * @param mf            Mutual Fund to replace or add
     * @throws Exception
     */
    public void mergeRecord(MutualFund mf) throws Exception {
        MutualFund mfWithSymbol = mfRepo.findMutualFundByFundSymbol(mf.getFundSymbol());
        if (mfWithSymbol != null) {
            // merge with any existing object in the repository
            mf = mfWithSymbol.merge(mf);
        }
        // store the record in the repository
        mfRepo.save(mf);
    }

    /**
     * Parse category name and inception date from finance.yahoo.com and update repository
     * @param symbol       Symbols of mutual funds to parse and update to repository
     * @throws Exception
     */
    public MutualFund parseMutualFundMetaData(String symbol) throws Exception {
        // URL corresponding to the symbol
        URL url = new URL(rootURLMutualFundMetaData + symbol);
        try( InputStream in = url.openStream() ) {
            // get HTML of fund website
            Scanner sc = new Scanner(in, "UTF-8");

            String content = sc.useDelimiter("\\A").next();
            // get <td> element containing category value from fund website HTML
            String categoryValueTDElement = XHTMLParser.parseElementWithPattern(content, "<td",
                    categoryValueField);

            // get <td> element containing inception date from fund website HTML
            String inceptionDateTDElement = XHTMLParser.parseElementWithPattern(content, "<td",
                    inceptionDateField);

            // parse category value from <td> element
            Document doc;
            Element span;
            String categoryName = null;
            if (categoryValueTDElement != null) {
                doc = Jsoup.parse(categoryValueTDElement);
                span = doc.select("span").first();
                if (span != null) {
                    categoryName = span.text();
                }
            }
            Date inceptionDate = null;
            // parse inception date from <td> element
            if (inceptionDateTDElement != null) {
                doc = Jsoup.parse(inceptionDateTDElement);
                span = doc.select("span").first();
                if (span != null) {
                    String inceptionDateString = span.text();
                    inceptionDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).
                            parse(inceptionDateString);
                }
            }
            // get enum-compatible version of category name
            GroupNameLookup lookup = GroupNameLookup.getInstance();
            String categoryNameEnumStr = lookup.toEnumFormat(categoryName);

            // use category name (enum format) to get category group
            String categoryGroup = getCategoryGroup(categoryNameEnumStr, lookup);

            // convert category group value to display format similar to that of category name
            categoryGroup = lookup.toDisplayFormat(categoryGroup);

            // save new mutual fund with category name, category group, and inception date to repository
            MutualFund mf = new MutualFund(symbol, categoryName, categoryGroup, inceptionDate);
            mergeRecord(mf);
            return mf;
        }
    }

    static String getCategoryGroup(String categoryName, GroupNameLookup lookup) {
        // special case for fund with Target Date category
        if (categoryName.contains("TARGET_DATE")) {
            return CategoryGroup.ALLOCATION.toString();
        }
        else if (categoryName.contains("ALLOCATION")) {
            return CategoryGroup.ALLOCATION.toString();
        }
        else if (categoryName == null || categoryName.equals("")) {
            return CategoryGroup.GROUP_UNKNOWN.toString();
        }
        else {
            CategoryName name;
            try {
                name = CategoryName.valueOf(categoryName);
            }
            catch (IllegalArgumentException e) {
                name = CategoryName.NAME_UNKNOWN;
            }
            String categoryGroup = lookup.nameToGroup(name).toString();
            return categoryGroup;
        }
    }
}
