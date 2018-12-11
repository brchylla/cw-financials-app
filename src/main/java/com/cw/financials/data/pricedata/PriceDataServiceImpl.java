package com.cw.financials.data.pricedata;

import com.cw.financials.data.SiteAccess;
import com.cw.financials.data.SiteAccessService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


/**
 * Created by ben chylla on 7/26/18.
 * Aggregates a PriceDataRepository for additional searching and downloading capability
 */
@Service
public class PriceDataServiceImpl implements PriceDataService {
    private static final Duration defaultIgnoreTime = Duration.ofHours(12);
    private static final String priceDataURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol={symbol}" +
            "&outputsize=full&datatype=csv&apikey=QR97L3MI1D3VNJEN";

    @Autowired
    PriceDataRepository pdRepo;
    @Autowired
    SiteAccessService siteService;
    
    private static final int INTERVAL_DAYS = 7;

    /**
     * Default constructor that uses autowiring
     */
    public PriceDataServiceImpl() {}

    public PriceDataRepository getRepository() {
        return pdRepo;
    }

    public void setRepository(PriceDataRepository pdRepo) {
        this.pdRepo = pdRepo;
    }

    /**
     * Contructor
     * @param repo  Repository to aggregate
     */
    public PriceDataServiceImpl(PriceDataRepository repo) {
        pdRepo = repo;
    }

    /**
     * Asynchronous calls syncPriceData
     *
     * @param symbol
     * @param url   Symbol to parse for price data
     * param  ignoreTime    Ignore this request if the site has been synchronized within a time period less than this duration
     *                 from the present
     * @param ignorePeriod
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    @Override
    public boolean syncPriceDataAsync(String symbol, URL url, Duration ignorePeriod) throws MalformedURLException,
            ProtocolException, IOException, URISyntaxException {
        boolean completed = true;
        String urlString = url.toString();
        if ( !siteService.accessedWithin(urlString, ignorePeriod) ) {
            completed = false;
            // calculate midnight on today's date
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            Date midnight = date.getTime();
            // skip syncing process and return true if site was accessed and fully synced since midnight today
            if (siteService.upToDate(urlString, midnight)) {
                completed = true;
            }
            // sync price data from site if not already doing so
            else if (!siteService.isUpdating(urlString)) {
                SiteAccess sa2 = new SiteAccess(urlString, new Date());
                siteService.getRepo().save(sa2);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        syncPriceData(symbol, url);
                        sa2.setLastCompleted(new Date());
                        siteService.getRepo().save(sa2);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        return completed;
    }

    interface PriceDataItemSetter {
        void setValue(PriceData pd,String value) throws Exception;
    }

    public static URL getStreamURL(String symbol) throws Exception {
        String urlString = priceDataURL.replace("{symbol}",symbol);
        return new URL(urlString);
    }

    public PriceData getLatestItemBySymbol(String symbol) {
        PageRequest request = new PageRequest(0, 1, new Sort(Sort.Direction.DESC,
                "timestamp"));
        List<PriceData> items = pdRepo.findPriceDataBySymbol(symbol, request);
        return items.isEmpty() ? null : items.get(0);
    }

    /**
     * Synchronizes price data entries in the repository with data from external site
     * @param symbol
     * @param url
     */
    @Override
    public void syncPriceData(String symbol, URL url) {
        int numEntries = 0;
        PriceData item = getLatestItemBySymbol(symbol);
        try {
            System.err.println(url.toString());
            Date maxDate = (item == null) ? null : item.timestamp;
            URLConnection conn = url.openConnection();
            System.out.println("Synchronizing price data...");
            PriceDataItemSetter timestampSetter = new PriceDataItemSetter() {
                @Override
                public void setValue(PriceData pd, String dateString) throws Exception {
                    // Code that parses a date from a string
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(dateString);
                    pd.setTimestamp(date);
                }
            };
            PriceDataItemSetter openPriceSetter = new PriceDataItemSetter() {
                @Override
                public void setValue(PriceData pd, String value) throws Exception {
                    // Code that parses value of "open" in CSV to float and sets openPrice in PriceData object
                    pd.setOpenPrice(Float.parseFloat(value));
                }
            };
            PriceDataItemSetter closePriceSetter = new PriceDataItemSetter() {
                @Override
                public void setValue(PriceData pd, String value) throws Exception {
                    // Code that parses value of "open" in CSV to float and sets openPrice in PriceData object
                    pd.setClosePrice(Float.parseFloat(value));
                }
            };
            PriceDataItemSetter shareVolumeSetter = new PriceDataItemSetter() {
                @Override
                public void setValue(PriceData pd, String value) throws Exception {
                    // Code that parses value of "open" in CSV to float and sets openPrice in PriceData object
                    pd.setShareVolume(Float.parseFloat(value));
                }
            };
            Map<String,PriceDataItemSetter> setterMap = new HashMap<>();
            setterMap.put("timestamp", timestampSetter);
            setterMap.put("open", openPriceSetter);
            setterMap.put("close", closePriceSetter);
            setterMap.put("volume", shareVolumeSetter);
            long maxDateTicks = (maxDate == null) ? Integer.MIN_VALUE : maxDate.getTime();
            try (InputStream inputStream = conn.getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                String[] headers = line.split(",");
                PriceDataItemSetter[] valueSetters = new PriceDataItemSetter[headers.length];
                for(int k=0; k<headers.length; k++) {
                    valueSetters[k] = setterMap.get(headers[k]);
                }
                line = reader.readLine();
                while( (line != null) && (line.length() > 0) ) {
                    String[] values = line.split(",");
                    PriceData pd = new PriceData();
                    pd.setSymbol(symbol);
                    for(int k=0; k<headers.length; k++) {
                        String itemValue = values[k];
                        PriceDataItemSetter setter = valueSetters[k];
                        if ( setter != null) {
                            try {
                                setter.setValue(pd, itemValue);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (pd.timestamp != null) {
                    	long timestampTicks = pd.timestamp.getTime();
                        if ((maxDate != null) && timestampTicks <= maxDateTicks) {
                            break;
                        }
                    }
                    // Congrats, you have just populated a PriceData object with one row of CSV data!
                    pdRepo.save(pd); // save PriceData object to repository if not already stored
                    numEntries++;
                    line = reader.readLine();
                }
            }
        } catch (SocketException socketException) {
            // DO nothing. This is expected at end of stream
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        	System.out.println("Done synchronizing " + numEntries + " entries of price data");
        }
    }

    /**
     * Search for list of price data entries by symbol, page number, and max entries
     * @param symbol    Symbol with contain logic
     * @param max           Maximum entries to return in list
     * @return              List of price data entries that contain symbol subject to max length
     */
    public List<PriceData> getPriceData(String symbol, int pageNo, int max) {
        return pdRepo.findPriceDataBySymbol(symbol.toUpperCase(), new PageRequest(pageNo, max));
    }

    public PriceHistory getPriceHistory(String symbol, Date startDate) {
        try {
            URL url = getStreamURL(symbol);
            boolean completed = syncPriceDataAsync(symbol, url, defaultIgnoreTime);
            // get list of price data objects after start date
            List<PriceData> pdList = pdRepo.findBySymbolAndTimestampAfter(symbol, startDate);
            // return price history with symbol, start date, price data list, and completion status of price data sync
            Date endDate = new Date(); // current date & time serves as end date
            PriceHistory ph = new PriceHistory(symbol, startDate, endDate, INTERVAL_DAYS, completed, pdList);
            return ph;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}