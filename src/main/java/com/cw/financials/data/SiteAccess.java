package com.cw.financials.data;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Tracks the last time that content related to a given site was updated
 * Created by rogerchylla on 8/5/18.
 */
public class SiteAccess {
    // The location of the site on the web
    @Indexed
    private String uri;
    // The date that content for the site was last synced (updated)
    private Date lastUpdated;
    // The date that content for the site was last updated
    private Date lastCompleted;

    // Default constructor
    public SiteAccess() {}

    public SiteAccess(String uriArg) {
        this.uri = uriArg;
        this.lastUpdated = null;
        this.lastCompleted = null;
    }

    public SiteAccess(String uriArg, Date updateDateArg) {
        this.uri = uriArg;
        this.lastUpdated = updateDateArg;
        this.lastCompleted = null;
    }

    public SiteAccess(String uriArg, Date updateDateArg, Date completionDateArg) {
        this.uri = uriArg;
        this.lastUpdated = updateDateArg;
        this.lastCompleted = completionDateArg;
    }

    public String getUri() { return uri; }

    public void setUri(String uri) { this.uri = uri; }

    public Date getLastUpdated() { return lastUpdated; }

    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public Date getLastCompleted() { return lastCompleted; }

    public void setLastCompleted(Date lastCompleted) { this.lastCompleted = lastCompleted; }
    
    @Override
    public String toString() {
    	if (lastCompleted != null) {
    		return "Last completed access: " + lastCompleted;
    	}
    	else if (lastUpdated != null) {
    		return "Last attempted access: " + lastUpdated;
    	}
    	else {
    		return "No access attempt made.";
    	}
    }
}
