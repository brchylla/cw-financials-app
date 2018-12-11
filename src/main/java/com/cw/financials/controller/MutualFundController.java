package com.cw.financials.controller;

import com.cw.financials.data.mutualfund.MutualFund;
import com.cw.financials.data.mutualfund.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MutualFundController {
    @Autowired
    MutualFundService mfService;

    @RequestMapping(value="/find",method = RequestMethod.GET)
    public MutualFund mutualFund(@RequestParam(value="symbol") String symbol) {
        return mfService.getRepository().findMutualFundByFundSymbol(symbol);
    }

    @RequestMapping(value="/query", method = RequestMethod.GET)
    public List<MutualFund> mutualFundList(@RequestParam(value="symbol") String symbol,
                                           @RequestParam(value="pageNo") int pageNo,
                                           @RequestParam(value="maxEntries") int maxEntries) {
        return mfService.findMutualFundsByFundSymbolContains(symbol, pageNo, maxEntries);
    }

    @RequestMapping(value="/count", method = RequestMethod.GET)
    public int mutualFundCount(@RequestParam(value="symbol") String symbol) {
        return mfService.getRepository().countMutualFundsByFundSymbolContains(symbol);
    }

    @RequestMapping(value="/getMFWithCategoryData", method = RequestMethod.GET)
    public MutualFund getMFWithCategoryData(@RequestParam(value="symbol") String symbol) {
        try {
            return mfService.parseMutualFundMetaData(symbol);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
