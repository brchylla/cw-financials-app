package com.cw.financials.data.mutualfund;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MutualFundServiceImplTest {

	GroupNameLookup lookup = GroupNameLookup.getInstance();
	
	@Test
	public void testGetCategoryGroup() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup("MUNI_NEW_YORK_INTERMEDIATE", lookup), 
				CategoryGroup.MUNICIPAL_BOND.toString());
	}
	
	@Test
	public void testGetCategoryGroupWithNameContainingTargetDate() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup("TARGET_DATE_2049", lookup), 
				CategoryGroup.ALLOCATION.toString());
	}
	
	@Test
	public void testGetCategoryGroupWithNameContainingAllocation() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup("TACTICAL_ALLOCATION", lookup), 
				CategoryGroup.ALLOCATION.toString());
	}
	
	@Test
	public void testGetCategoryGroupWithNameNull() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup(null, lookup), 
				CategoryGroup.GROUP_UNKNOWN.toString());
	}
	
	@Test
	public void testGetCategoryGroupWithNameEmpty() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup("", lookup), 
				CategoryGroup.GROUP_UNKNOWN.toString());
	}
	
	@Test
	public void testGetCategoryGroupWithNameWhitespace() {
		assertEquals(MutualFundServiceImpl.getCategoryGroup(" ", lookup), 
				CategoryGroup.GROUP_UNKNOWN.toString());
	}

}
