package com.cw.financials.data.pricedata;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.cw.financials.data.pricedata.PriceData;
import com.cw.financials.data.pricedata.PriceDataInterval;

public class PriceDataIntervalTest {

	// format for displaying start and end dates
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@Test
	public void testOneAccumulateCall() {
		PriceDataInterval pdi = new PriceDataInterval();
		
		// Date object representing the present date & time
		Date now = new Date();
		
		// accumulate the PriceDataInterval with only one PriceData object
		PriceData pd = new PriceData("SUEBG", now, 11, 10, 12);
		pdi.accumulate(pd);
		
		// Expected: high = 10, epsilon = 0
		assertEquals(pdi.getHigh(), 10, 0);
		// Expected: low = 10, epsilon = 0
		assertEquals(pdi.getLow(), 10, 0);
		// Expected: mean = 10, epsilon = 0
		assertEquals(pdi.getMean(), 10, 0);
		// Expected: startDate = now
		assertEquals(pdi.getStartDate().equals(df.format(now)), true);
		// Expected: endDate = now
		assertEquals(pdi.getEndDate().equals(df.format(now)), true);
	}
	
	@Test
	public void testMultipleAccumulateCalls() {
		PriceDataInterval pdi = new PriceDataInterval();
		
		// Date object representing the exact date & time 24 hours ago
		final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    Date yesterday = cal.getTime();
	    
	    // Date representing the present date & time
		Date today = new Date();
		
		// accumulate the PriceDataInterval with two PriceData objects
		PriceData pd1 = new PriceData("SUEBG", yesterday, 11, 10, 12);
		PriceData pd2 = new PriceData("SUEBG", today, 6, 5, 7);
		pdi.accumulate(pd1);
		pdi.accumulate(pd2);
		
		// Expected: high = 10, epsilon = 0
		assertEquals(pdi.getHigh(), 10, 1);
		// Expected: low = 5, epsilon = 0
		assertEquals(pdi.getLow(), 5, 1);
		// Expected: mean = 7.5, epsilon = 0
		assertEquals(pdi.getMean(), 7.5, 0);
		// Expected: startDate = yesterday
		assertEquals(pdi.getStartDate().equals(df.format(yesterday)), true);
		// Expected: endDate = today
		assertEquals(pdi.getEndDate().equals(df.format(today)), true);
	}

}
