package com.cw.financials.data.pricedata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public interface PriceDataService {
    PriceDataRepository getRepository();

    PriceData getLatestItemBySymbol(String symbol);

    /**
     * Asynchronous calls syncPriceData
     *
     * @param symbol
     * @param url   Site to parse for price data
     * @param ignorePeriod
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    boolean syncPriceDataAsync(String symbol, URL url, Duration ignorePeriod) throws MalformedURLException,
            ProtocolException, IOException, URISyntaxException;

    /**
     * Synchronizes price data entries in the repository with data from external site
     * @param symbol
     * @param url
     */
    void syncPriceData(String symbol, URL url);

    /**
     * Search for list of price data entries by symbol, page number, and max entries
     * @param symbol    Symbol with contain logic
     * @param max           Maximum entries to return in list
     * @return              List of price data entries that contain symbol subject to max length
     */
    List<PriceData> getPriceData(String symbol, int pageNo, int max);

    PriceHistory getPriceHistory(String symbol, Date startDate);
}