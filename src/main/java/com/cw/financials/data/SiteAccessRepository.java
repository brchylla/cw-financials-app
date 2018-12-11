package com.cw.financials.data;

import com.cw.financials.data.SiteAccess;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

/**
 * Created by rogerchylla on 8/5/18.
 */

/**
 * A repository of site access records
 */
public interface SiteAccessRepository extends MongoRepository<SiteAccess,String> {
    /**
     * Get most recent access to site with given URI
     * @param uri       URI of site
     * @return          The most recent access to the site
     */
    SiteAccess findSiteAccessByUriOrderByLastUpdatedDesc(String uri);

    /**
     * Get most recent access (where update was fully completed) to site with given URI
     * @param uri       URI of site
     * @return          The most recent access with fully completed update
     */
    SiteAccess findSiteAccessByUriOrderByLastCompletedDesc(String uri);
}
