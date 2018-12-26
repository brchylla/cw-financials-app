package com.cw.financials.data.mutualfund;

import static org.junit.Assert.*;

import org.junit.Test;

public class GroupNameLookupTest {

	private static final GroupNameLookup lookup = GroupNameLookup.getInstance();
	
	@Test
	public void testToEnumFormat() {
		assertEquals(lookup.toEnumFormat("Foreign Small Mid Value"), 
				CategoryName.FOREIGN_SMALL_MID_VALUE.toString());
	}
	
	@Test
	public void testCategoryToEnumWithNameEmpty() {
		assertEquals(lookup.toEnumFormat(""), 
				CategoryName.NAME_UNKNOWN.toString());
	}
	
	@Test
	public void testCategoryToEnumWithNameWhitespace() {
		assertEquals(lookup.toEnumFormat(" "), 
				CategoryName.NAME_UNKNOWN.toString());
	}
	
	@Test
	public void testCategoryToEnumWithNameNull() {
		assertEquals(lookup.toEnumFormat(null), 
				CategoryName.NAME_UNKNOWN.toString());
	}
	
	@Test
	public void testCategoryToEnumFormatWithNameContainingSTK() {
		assertEquals(lookup.toEnumFormat("Pacific/Asia ex-Japan Stk"), 
				CategoryName.PACIFIC_ASIA_EX_JAPAN_STOCK.toString());
	}
	
	@Test
	public void testCategoryToEnumFormatWithNameContainingMKTS() {
		assertEquals(lookup.toEnumFormat("Diversified Emerging Mkts"), 
				CategoryName.DIVERSIFIED_EMERGING_MARKETS.toString());
	}
	
	@Test
	public void testCategoryToEnumFormatWithNameContainingINTERM() {
		assertEquals(lookup.toEnumFormat("Muni Single State Interm"), 
				CategoryName.MUNI_SINGLE_STATE_INTERMEDIATE.toString());
	}

	@Test
	public void testToDisplayFormat() {
		assertEquals(lookup.toDisplayFormat("PACIFIC_ASIA_EX_JAPAN_STOCK"), "Pacific Asia Ex Japan Stock");
	}
	
	@Test
	public void testUnknownNameToDisplayFormat() {
		// special cases for N/A, NAME_UNKNOWN, or GROUP_UNKNOWN
		assertEquals(lookup.toDisplayFormat("N/A"), "N/A");
		assertEquals(lookup.toDisplayFormat("NAME_UNKNOWN"), "N/A");
		assertEquals(lookup.toDisplayFormat("GROUP_UNKNOWN"), "N/A");
	}

	@Test
	public void testNameToGroup() {
		assertEquals(lookup.nameToGroup(CategoryName.TRADING_LEVERAGED_COMMODITIES), 
				CategoryGroup.ALTERNATIVE);
	}
	
	@Test
	public void testNameToGroupLowerBound() {
		assertEquals(lookup.nameToGroup(CategoryName.LARGE_VALUE), 
				CategoryGroup.US_EQUITY);
	}
	
	@Test
	public void testNameToGroupUpperBound() {
		assertEquals(lookup.nameToGroup(CategoryName.NAME_UNKNOWN), 
				CategoryGroup.GROUP_UNKNOWN);
	}
	
	@Test
	public void testIsNumeric() {
		// test with numeric value
		assertEquals(GroupNameLookup.isNumeric("10.0"), true);
		
		// test with non-numeric value
		assertEquals(GroupNameLookup.isNumeric("Bob"), false);
	}

}
