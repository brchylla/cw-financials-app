package com.cw.financials.data.pricedata;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class PriceHistoryTest {

	@Test
	public void testPriceHistory() {
		// Symbol of Mutual Fund that the Price History corresponds to
		final String SYMBOL = "SUEBG";
		
		// Date object representing the exact date & time 1 week ago
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -2);
		final Date BEGINNING_DATE = cal.getTime();
		
		// Date object representing current date & time
		Date END_DATE = new Date();
		
		// Number of days in a Price Data Interval
		final int INTERVAL_DAYS = 7;
		
		// This should have NO impact on the test
		final boolean COMPLETED = true;
		
		// List of TWO Price Data objects w/ timestamps within one week before END_DATE
		List<PriceData> pdList = new ArrayList<>();
		pdList.add(new PriceData(SYMBOL, END_DATE, 11, 10, 12));
		cal.add(Calendar.DATE, -4);
		pdList.add(new PriceData(SYMBOL, cal.getTime(), 9, 7, 10));
		
		// Add ONE more Price Data object >1 week before END_DATE
		cal.add(Calendar.DATE, -4);
		pdList.add(new PriceData(SYMBOL, cal.getTime(), 12, 10, 12));
		
		// Create Price History object using set variables
		PriceHistory ph = new PriceHistory(SYMBOL, BEGINNING_DATE, END_DATE, 
				INTERVAL_DAYS, COMPLETED, pdList);
		
		// Test for correct number of intervals
		assertEquals(ph.getIntervals().length, 2);
		
		// Test for correct count in first Price Data Interval
		assertEquals(ph.getIntervals()[0].count, 2);
		
		// Test for correct count in next Price Data Interval
		assertEquals(ph.getIntervals()[1].count, 1);
		
		// Test for correct percent change in the first (more recent) interval
		// DIFFERENCE = ((10 + 7)/2) - 10 = 8.5 - 10 = -1.5
		// MIDPOINT = 0.5 * (((10+ 7)/2) + 10) = 0.5 * (8.5 + 10) = 9.25
		// PERCENT_CHANGE = (-1.5 / 9.25) * 100% = -16%
		assertEquals(ph.getIntervals()[0].getPercentChange(), -16, 0.5);
	}

}
