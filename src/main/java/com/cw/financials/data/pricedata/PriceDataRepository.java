package com.cw.financials.data.pricedata;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Created by ben chylla on 7/26/18.
 */
public interface PriceDataRepository extends MongoRepository<PriceData, String> {
    /**
     * Search for paginated list of price data entries with symbol
     * @param symbol        Fund symbol for price data entries
     * @param pageable      Object that controls paging
     * @return              List of price data entries that satisfy query and paging
     */
    List<PriceData> findPriceDataBySymbol(String symbol, Pageable pageable);

    /**
     * Search for list of price data entries with symbol after date
     * @param symbol        Fund symbol for price data entries
     * @param startDate     Starting date for price data entries
     * @return              List of price data entries that satisfy query
     */
    List<PriceData> findBySymbolAndTimestampAfter(String symbol, Date startDate);
}
