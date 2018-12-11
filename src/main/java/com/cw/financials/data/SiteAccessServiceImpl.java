package com.cw.financials.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by rogerchylla on 8/5/18.
 */
@Service
class SiteAccessServiceImpl implements SiteAccessService {
    @Autowired
    SiteAccessRepository siteRepo;

    @Override
    public SiteAccessRepository getRepo() {
        return siteRepo;
    }

    @Override
    public boolean accessedWithin(String uri, Duration ignorePeriod) {
        SiteAccess sa = siteRepo.findSiteAccessByUriOrderByLastCompletedDesc(uri);
        boolean flag = false;
        if ( sa != null && sa.getLastCompleted() != null ) {
            long duration = (System.currentTimeMillis() - sa.getLastCompleted().getTime());
            flag = Duration.ofMillis(duration).getSeconds() <= ignorePeriod.getSeconds();
        }
        return flag;
    }

    public boolean upToDate(String uri, Date reqTime) {
        SiteAccess sa = siteRepo.findSiteAccessByUriOrderByLastCompletedDesc(uri);
        // if site was accessed and completed updating since required time, caller program is up-to-date
        boolean result = false;
        if ( (sa != null) && (sa.getLastCompleted() != null) && sa.getLastCompleted().after(reqTime) ) {
            result = true;
        }
        return result;
    }

    public boolean isUpdating(String uri) {
        SiteAccess sa = siteRepo.findSiteAccessByUriOrderByLastUpdatedDesc(uri);
        // if site was accessed and contains an update time but no completion time, caller is still updating from site
        boolean result = false;
        if ( sa != null && sa.getLastUpdated() != null && sa.getLastCompleted() == null ) {
            result = true;
        }
        return result;
    }

    public boolean notUpdating(String uri) {
        SiteAccess sa = siteRepo.findSiteAccessByUriOrderByLastUpdatedDesc(uri);
        // if site was accessed and contains neither an update nor a completion time, caller isn't updating from site
        boolean result = false;
        if ( sa != null && sa.getLastUpdated() == null && sa.getLastCompleted() == null ) {
            result = true;
        }
        return result;
    }
}
