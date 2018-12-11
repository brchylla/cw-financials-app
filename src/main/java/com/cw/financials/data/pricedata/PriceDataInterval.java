package com.cw.financials.data.pricedata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceDataInterval {
    float high;
    float low;
    float mean;

    // used to calculate mean; updated by .accumulate
    float sum;
    int count;
    
    // set by PriceHistory class
    float percentChange; // % change compared to mean of previous interval in price history
    
    // start and end dates stored for use by client
    Date startDate;
    Date endDate;
    
    // format for displaying start and end dates
    static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public PriceDataInterval() {
        high = 0;
        low = 0;
        mean = 0;
    	sum = 0;
        count = 0;
        percentChange = 0;
        startDate = null;
        endDate = null;
    }

    public void accumulate(PriceData pd) {
        // special case: if interval is empty, initialize everything
    	if (count == 0) {
    		high = pd.closePrice;
    		low = pd.closePrice;
    		mean = pd.closePrice;
    		sum = pd.closePrice;
    		count = 1;
    		// initially set start and end dates to price data timestamp
    		startDate = pd.timestamp;
    		endDate = pd.timestamp;
    	}
    	else {
    		// update all data
        	sum += pd.closePrice;
            count++;
            mean = sum/count;
            if (pd.closePrice > high) {
               high = pd.closePrice;
            }
            else if (pd.closePrice < low) {
               low = pd.closePrice;
            }
            // if timestamp is later than end date, reset end date
            if (pd.timestamp.after(endDate)) {
            	endDate = pd.timestamp;
            }
            // if timestamp is earlier than start date, reset start date
            else if (pd.timestamp.before(startDate)) {
            	startDate = pd.timestamp;
            }
    	}
    }
    
    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getMean() {
        return mean;
    }
    
    public float getPercentChange() {
    	return percentChange;
    }
    
    public void setPercentChange(float percentChange) {
    	this.percentChange = percentChange;
    }

    public String getStartDate() {
    	return df.format(startDate);
    }
    
    public void setStartDate(Date startDate) {
    	this.startDate = startDate;
    }
    
    public String getEndDate() {
    	return df.format(endDate);
    }
    
    public void setEndDate(Date endDate) {
    	this.endDate = endDate;
    }
}
