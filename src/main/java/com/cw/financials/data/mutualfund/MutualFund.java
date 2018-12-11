package com.cw.financials.data.mutualfund;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

public class MutualFund {

    @Id
    String _id; // Used to access the ObjectID in the repository
    @Indexed
    private String fundSymbol; // the identifier for each mutual fund or money market
    @Indexed
    private String fundName; // the name of the mutual fund
    private String fundFamilyName; // the name of the fund company that is responsible for the fund or money market

    private String categoryName; // the name of the category for the fund
    private String categoryGroup; // the name of the group for the category of the fund
    private Date inceptionDate; // the inception date of the fund

    public MutualFund() {}

    public MutualFund(String fundSymbol, String fundName) {
        this.fundSymbol = fundSymbol;
        this.fundName = fundName;
    }

    public MutualFund(String fundSymbol, String fundName, String fundFamilyName) {
        this.fundSymbol = fundSymbol;
        this.fundName = fundName;
        this.fundFamilyName = fundFamilyName;
    }

    public MutualFund(String fundSymbol, String categoryName, String categoryGroup, Date inceptionDate) {
        this.fundSymbol = fundSymbol;
        this.categoryName = categoryName;
        this.categoryGroup = categoryGroup;
        this.inceptionDate = inceptionDate;
    }

    public MutualFund(String fundSymbol, String fundName, String categoryName, String categoryGroup,
                      Date inceptionDate) {
        this.fundSymbol = fundSymbol;
        this.fundName = fundName;
        this.categoryName = categoryName;
        this.categoryGroup = categoryGroup;
        this.inceptionDate = inceptionDate;
    }

    public MutualFund(String fundSymbol, String fundName, String fundFamilyName,
                      String categoryName, String categoryGroup, Date inceptionDate) {
        this.fundSymbol = fundSymbol;
        this.fundName = fundName;
        this.fundFamilyName = fundFamilyName;
        this.categoryName = categoryName;
        this.categoryGroup = categoryGroup;
        this.inceptionDate = inceptionDate;
    }

    public String getId() {
        return _id;
    }

    public String getFundSymbol() {
        return fundSymbol;
    }

    public String getFundName() {
        return fundName;
    }

    public String getFundFamilyName() {
        return fundFamilyName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public void setFundSymbol(String fundSymbol) {
        this.fundSymbol = fundSymbol;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public void setFundFamilyName(String fundFamilyName) {
        this.fundFamilyName = fundFamilyName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public MutualFund merge(MutualFund newObject) {
        MutualFund union;
        try {
            union = (MutualFund)newObject.clone();
        }
        catch (CloneNotSupportedException e) {
            // do nothing
            union = newObject;
        }
        if ( union.getId() == null) {
            union.setId(getId());
        }
        if (union.getFundSymbol() == null) {
            union.setFundSymbol(this.fundSymbol);
        }
        if (union.getFundName() == null) {
            union.setFundName(this.fundName);
        }
        if (union.getFundFamilyName() == null) {
            union.setFundFamilyName(this.fundFamilyName);
        }
        if (union.getCategoryName() == null) {
            union.setCategoryName(this.categoryName);
        }
        if (union.getCategoryGroup() == null) {
            union.setCategoryGroup(this.categoryGroup);
        }
        if (union.getInceptionDate() == null) {
            union.setInceptionDate(this.inceptionDate);
        }
        return union;
    }

    @Override
    public String toString() {
        return String.format(
                "MutualFund[fundSymbol=%s, fundName='%s', categoryName='%s', categoryGroup='%s']",
                fundSymbol, fundName, categoryName, categoryGroup);
    }

    /**
     * Returns the CategoryGroup instance mapped to the specified name
     * @param name  CategoryName value
     * @return      Group mapped to this name
     */
    public static final CategoryGroup getGroupForName(CategoryName name) {
        return CategoryGroup.GROUP_UNKNOWN;
    }
}
