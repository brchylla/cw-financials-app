package com.cw.financials.controller;

import com.cw.financials.data.pricedata.PriceData;
import com.cw.financials.data.pricedata.PriceDataService;
import com.cw.financials.data.pricedata.PriceHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class PriceDataController {
    @Autowired
    PriceDataService pdService;

    @RequestMapping(value="/query/priceData", method = RequestMethod.GET)
    public List<PriceData> priceDataList(@RequestParam(value="symbol") String symbol,
                                           @RequestParam(value="pageNo") int pageNo,
                                           @RequestParam(value="maxEntries") int maxEntries) {
        return pdService.getPriceData(symbol, pageNo, maxEntries);
    }

    @RequestMapping(value="/query/priceQuote", method = RequestMethod.GET)
    public PriceHistory priceQuote(@RequestParam(value="symbol") String symbol,
                                 @RequestParam(value="startDate") String startDate) throws Exception {
        return pdService.getPriceHistory(symbol, new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
    }
}
