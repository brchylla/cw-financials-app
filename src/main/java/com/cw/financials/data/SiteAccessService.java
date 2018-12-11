package com.cw.financials.data;

import java.time.Duration;
import java.util.Date;

/**
 * Created by rogerchylla on 8/5/18.
 */
public interface SiteAccessService {
    SiteAccessRepository getRepo();

    /**
     * Gets whether the specified site has been accessed and fully updated since the given duration
     * @param uri           URI of the site
     * @param duration      Time duration
     * @return              Whether the site has been updated within a time <= duration relative to present
     */
    boolean accessedWithin(String uri, Duration duration);

    /**
     * Gets whether the specified site has been accessed and fully updated since the specified required time
     * @param uri           URI of the site
     * @param reqTime       Time of required update
     * @return              Whether the site has been updated since required time
     */
    boolean upToDate(String uri, Date reqTime);

    /**
     * Gets whether the specific site has been accessed but not fully updated
     * @param uri           URI of the site
     * @return              Whether the site has been accessed but is still currently updating
     */
    boolean isUpdating(String uri);

    /**
     * Gets whether the specific site has been accessed but will not update
     * @param uri           URI of the site
     * @return              Whether the site has been accessed but has not even started updating
     */
    boolean notUpdating(String uri);
}
