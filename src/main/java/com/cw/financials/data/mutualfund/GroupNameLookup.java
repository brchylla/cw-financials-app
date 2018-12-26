package com.cw.financials.data.mutualfund;

/**
 * Class used to perform group lookups from a category name. It it local to this package
 * and not public.
 * This class uses the singleton pattern:
 */
class GroupNameLookup {
    private CategoryGroup[] lookupTable;

    private static final GroupNameLookup singleton = new GroupNameLookup();

    // Protected constructor for singleton pattern
    protected GroupNameLookup() {}

    protected CategoryGroup[] getLookupTable() {
        if ( lookupTable == null) {
            // Lookup table contains one item for each category name
            lookupTable = new CategoryGroup[CategoryName.values().length];
            // populate lookup table with category groups corresponding to ranges
            CategoryGroup[] groups = CategoryGroup.values();
            int nGroups = groups.length;
            int[][] ranges = new int[2][nGroups];

            // staticly assign ranges to ordinals for all category names corresponding to all groups
            // Group: US Equity
            ranges[0][0] = CategoryName.LARGE_VALUE.ordinal();
            ranges[1][0] = CategoryName.LEVERAGED_NET_LONG.ordinal();
            // Group: Sector Equity
            ranges[0][1] = CategoryName.COMMUNICATIONS.ordinal();
            ranges[1][1] = CategoryName.MISCELLANEOUS_SECTOR.ordinal();
            // Group: Allocation
            ranges[0][2] = CategoryName.CONVERTIBLES.ordinal();
            ranges[1][2] = CategoryName.RETIREMENT_INCOME.ordinal();
            // Group: International Equity
            ranges[0][3] = CategoryName.FOREIGN_LARGE_VALUE.ordinal();
            ranges[1][3] = CategoryName.JAPAN_STOCK.ordinal();
            // Group: Alternative
            ranges[0][4] = CategoryName.BEAR_MARKET.ordinal();
            ranges[1][4] = CategoryName.TRADING_MISCELLANEOUS.ordinal();
            // Group: Commodities
            ranges[0][5] = CategoryName.COMMODITIES_AGRICULTURE.ordinal();
            ranges[1][5] = CategoryName.COMMODITIES_PRECIOUS_METALS.ordinal();
            // Group: Taxable Bond
            ranges[0][6] = CategoryName.LONG_GOVERNMENT.ordinal();
            ranges[1][6] = CategoryName.NONTRADITIONAL_BOND.ordinal();
            // Group: Municipal Bond
            ranges[0][7] = CategoryName.MUNI_NATIONAL_LONG.ordinal();
            ranges[1][7] = CategoryName.MUNI_PENNSYLVANIA.ordinal();
            // Group: Money Market
            ranges[0][8] = CategoryName.TAXABLE_MONEY_MARKET.ordinal();
            ranges[1][8] = CategoryName.TAX_FREE_MONEY_MARKET.ordinal();
            // Group Unknown
            ranges[0][9] = CategoryName.NAME_UNKNOWN.ordinal();
            ranges[1][9] = CategoryName.NAME_UNKNOWN.ordinal();
            // populate lookup table with ordinals corresponding to ranges
            for (int i = 0; i < nGroups; i++) {
                int firstIndex = ranges[0][i];
                int lastIndex = ranges[1][i];
                for (int j = firstIndex; j <= lastIndex; j++) {
                    lookupTable[j] = groups[i];
                }
            }
        }
        return lookupTable;
    }

    public static final GroupNameLookup getInstance() {
        return singleton;
    }

    public String toEnumFormat(String htmlName) {
        String newValue = "";
        if (htmlName == null || htmlName.trim().length() == 0) {
            return CategoryName.NAME_UNKNOWN.toString();
        }
        String[] words = htmlName.split(" |-|/");
        for (int i = 0; i < words.length; i++) {
            // special case for string "Stk" (stands for "Stock")
            if (words[i].toUpperCase().equals("STK")) {
                newValue += "STOCK";
            }
            // special case for string "Mkts" (stands for "Markets")
            else if (words[i].toUpperCase().equals("MKTS")) {
                newValue += "MARKETS";
            }
            // special case for string "Interm" (stands for "Intermediate")
            else if (words[i].toUpperCase().equals("INTERM")) {
                newValue += "INTERMEDIATE";
            }
            else {
                newValue += words[i].toUpperCase();
            }

            if (i < words.length - 1) {
                newValue += "_";
            }
        }
        return newValue;
    }

    public String toDisplayFormat(String enumName) {
        // special case for N/A, NAME_UNKNOWN, or GROUP_UNKNOWN
        if (enumName.equals("N/A") || enumName.contains("UNKNOWN")) {
            return "N/A";
        }
        String newValue = "";
        String[] words = enumName.split("_");
        // shift each word to lower case and capitalize its first letter
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 1) {
                newValue += words[i].toUpperCase().substring(0, 1) + words[i].toLowerCase().substring(1);
                if (i < words.length - 1) {
                    newValue += " ";
                }
            }
            else {
                newValue += words[i].toUpperCase();
            }
        }
        return newValue;
    }

    public CategoryGroup nameToGroup(CategoryName name) { return getLookupTable()[name.ordinal()]; }

    /**
     * Copied and pasted from Stack Overflow.
     * @author: CraigTP
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
