package com.worth.ifs.util;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Optional.ofNullable;

/**
 * Utility class to provide common use case wrappers that can be used to wrap callbacks that require either an entity or
 * some failure message if that entity cannot be found.
 */
public class EntityLookupCallbacks {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(EntityLookupCallbacks.class);

    public static <SuccessType> ServiceResult<SuccessType> getOrFail(
            Supplier<SuccessType> getterFn,
            Error failureResponse) {

        SuccessType getterResult = getterFn.get();

        if (getterResult instanceof Collection && ((Collection) getterResult).isEmpty()) {
            return serviceFailure(failureResponse);
        }

        return ofNullable(getterResult).map(ServiceResult::serviceSuccess).orElse(serviceFailure(failureResponse));
    }

    public static <T> ServiceResult<T> getOnlyElementOrFail(Collection<T> list) {
        if (list == null || list.size() != 1) {
            return serviceFailure(internalServerErrorError("Found multiple entries in list but expected only 1 - " + list));
        }
        return serviceSuccess(list.iterator().next());
    }
}
