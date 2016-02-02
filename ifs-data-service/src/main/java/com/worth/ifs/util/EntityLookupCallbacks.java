package com.worth.ifs.util;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static java.util.Optional.ofNullable;

/**
 * Utility class to provide common use case wrappers that can be used to wrap callbacks that require either an entity or
 * some failure message if that entity cannot be found.
 */
public class EntityLookupCallbacks {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(EntityLookupCallbacks.class);

    public static ServiceResult<ProcessRole> getProcessRoleById(Long processRoleId,
            ProcessRoleRepository processRoleRepository,
            Error noProcessRole) {

        return getOrFail(() -> processRoleRepository.findOne(processRoleId), noProcessRole);
    }

    public static ServiceResult<Response> getResponseById(Long responseId,
                                                          ResponseRepository responseRepository,
                                                          Error noResponseError) {

        return getOrFail(() -> responseRepository.findOne(responseId), noResponseError);
    }

    public static <SuccessType> ServiceResult<SuccessType> getOrFail(
            Supplier<SuccessType> getterFn,
            Error failureResponse) {

        return ofNullable(getterFn.get()).
                map(ServiceResult::serviceSuccess).
                orElse(serviceFailure(failureResponse));
    }
}
