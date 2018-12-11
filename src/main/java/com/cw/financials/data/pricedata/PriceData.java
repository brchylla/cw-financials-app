package com.cw.financials.data.pricedata;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceData {
    public String symbol;
    public Date timestamp;
    public float openPrice;
    public float closePrice;
    public float shareVolume;

    public PriceData() {}

    public PriceData(String symbol, Date timestamp, float openPrice, float closePrice, float shareVolume) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.shareVolume = shareVolume;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getShareVolume() {
        return shareVolume;
    }

    public void setShareVolume(float shareVolume) {
        this.shareVolume = shareVolume;
    }

    @Override
    public String toString() {
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return String.format(
                "PriceData[symbol=%s, timestamp='%s', openPrice='%s', closePrice='%s', shareVolume='%s']",
                symbol, format.format(timestamp), openPrice, closePrice, shareVolume);
    }
}
