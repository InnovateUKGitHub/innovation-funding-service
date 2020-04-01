package org.innovateuk.ifs.cache;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.Collection;

public class CacheHelper {

    public static boolean cacheResult(ServiceResult<?> result) {
        if (result.isFailure()) {
            return false;
        }

        Object payload = result.getSuccess();

        if (payload instanceof Collection) {
            CacheableWhenCompetitionOpen target = (CacheableWhenCompetitionOpen) ((Collection) payload).iterator().next();
            return target.isCompetitionOpen();
        }

        if (payload instanceof CacheableWhenCompetitionOpen) {
            return ((CacheableWhenCompetitionOpen) payload).isCompetitionOpen();
        }

        return false;
    }

}
