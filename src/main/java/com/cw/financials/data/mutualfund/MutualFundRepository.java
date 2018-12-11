package com.cw.financials.data.mutualfund;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by ben chylla on 7/23/18.
 */
public interface MutualFundRepository extends MongoRepository<MutualFund, String> {
    /**
     * Find a single mutual fund by symbol
     * @param fundSymbol    Symbol to use
     * @return              Fund that matches symbol (can be null)
     */
    MutualFund findMutualFundByFundSymbol(String fundSymbol); // find mutual fund using its symbol

    /**
     * Search for list of mutual funds that contain fundSymbol
     * @param fundSymbol    Symbol to contain
     * @param pageable      Object that controls paging
     * @return              List of mutual funds that satisfy query and paging
     */
    List<MutualFund> findMutualFundsByFundSymbolContains(String fundSymbol, Pageable pageable);

    /**
     * Get count of mutual funds that contain fundSymbol
     * @param fundSymbol    Symbol to contain
     * @return              Count of mutual funds that contain symbol
     */
    int countMutualFundsByFundSymbolContains(String fundSymbol);
}
