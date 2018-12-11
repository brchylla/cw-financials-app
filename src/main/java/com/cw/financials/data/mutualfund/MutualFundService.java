package com.cw.financials.data.mutualfund;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public interface MutualFundService {
    MutualFundRepository getRepository();

    /**
     * Asynchronous calls syncMutualFunds
     * @param url   Site to parse for mutual fund data
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    void syncMutualFundsAsync(URL url, Duration ignorePeriod) throws MalformedURLException,
            ProtocolException, IOException;

    /**
     * Synchronizes mutuala funds in the repository with data from external site
     * @param url   External site
     */
    void syncMutualFunds(URL url);

    /**
     * Search for list of mutual funds by symbol, page number, and max entries
     * @param fundSymbol    Symbol with contain logic
     * @param max           Maximum entries to return in list
     * @return              List of mutual funds that contain symbol subject to max length
     */
    List<MutualFund> findMutualFundsByFundSymbolContains(String fundSymbol, int pageNo, int max);

    /**
     * Parse mutual funds by symbol from Yahoo Finance website and update repository
     * @param symbol       Symbol of mutual fund to parse and update to repository
     * @return             The parsed mutual fund
     */
    MutualFund parseMutualFundMetaData(String symbol) throws Exception;

    /**
     * Updates an object that already exists in the repository or adds a new one
     * @param mf            Object to replace or add
     * @throws Exception
     */
    void mergeRecord(MutualFund mf) throws Exception;
}
