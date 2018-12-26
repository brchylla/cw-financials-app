package com.cw.financials.data.mutualfund;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class MutualFundTest {

	private static final String FUND_SYMBOL = "Test Symbol";
	private static final String FUND_NAME = "Test Name";
	private static final String FUND_FAMILY_NAME = "Test Family Name";
	private static final String CATEGORY_NAME = "Test Category Name";
	private static final String CATEGORY_GROUP = "Test Category Group";
	private static final Date INCEPTION_DATE = new Date();
	
	@Test
	public void testMerge() {
		MutualFund mutualFund = new MutualFund(FUND_SYMBOL, FUND_NAME, FUND_FAMILY_NAME);
		MutualFund mutualFundWithCategoryData = new MutualFund(FUND_SYMBOL, CATEGORY_NAME, CATEGORY_GROUP, INCEPTION_DATE);
		mutualFundWithCategoryData.merge(mutualFund);
		
		// Test to make sure that the two mutual funds were properly merged
		assertEquals(mutualFund.getFundSymbol(), FUND_SYMBOL);
		assertEquals(mutualFund.getFundName(), FUND_NAME);
		assertEquals(mutualFund.getFundFamilyName(), FUND_FAMILY_NAME);
		assertEquals(mutualFund.getCategoryName(), CATEGORY_NAME);
		assertEquals(mutualFund.getCategoryGroup(), CATEGORY_GROUP);
		assertEquals(mutualFund.getInceptionDate(), INCEPTION_DATE);
	}

}
