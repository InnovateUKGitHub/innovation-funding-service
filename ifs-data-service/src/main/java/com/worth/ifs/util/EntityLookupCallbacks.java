package com.worth.ifs.util;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.transactional.Error;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.NOT_FOUND_ENTITY;
import static com.worth.ifs.transactional.ServiceResult.serviceFailure;
import static java.util.Optional.ofNullable;

/**
 * Utility class to provide common use case wrappers that can be used to wrap callbacks that require either an entity or
 * some failure message if that entity cannot be found.
 */
public class EntityLookupCallbacks {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(EntityLookupCallbacks.class);

    /**
     * Given a user, a specific Role type and an Application, ensure that the user does indeed have that Role on the Application
     * and if so, call the wrapped code that in turn will either fail or succeed.  If the role is not found for the user and
     * application, return a failure as a JsonStatusResponse.
     *
     * @param userId
     * @param roleType
     * @param applicationId
     * @param serviceLocator
     * @return
     */
    public static ServiceResult<JsonStatusResponse> withProcessRoleReturnJsonResponse(Long userId,
            UserRoleType roleType, Long applicationId,
            ServiceLocator serviceLocator, Function<ProcessRole,
            ServiceResult<JsonStatusResponse>> doWithProcessRoleFn) {

        Error roleNotFoundError = new Error(NOT_FOUND_ENTITY, Role.class, roleType);
        Error processRoleNotFoundError = new Error(NOT_FOUND_ENTITY, ProcessRole.class, roleType, userId, applicationId);

        return getRoleForRoleType(roleType, serviceLocator.getRoleRepository(), roleNotFoundError)
                .map(role -> getProcessRoleForRoleUserAndApplication(role, userId, applicationId, serviceLocator.getProcessRoleRepository(), processRoleNotFoundError)
                .map(doWithProcessRoleFn::apply));
    }

    public static ServiceResult<Role> getRoleForRoleType(UserRoleType type,
             RoleRepository roleRepository, Error noRoleFound) {

        Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
        return matchingRole.map(ServiceResult::serviceSuccess).orElse(serviceFailure(noRoleFound));
    }

    public static ServiceResult<ProcessRole> getProcessRoleForRoleUserAndApplication(Role role, Long userId,
            Long applicationId, ProcessRoleRepository processRoleRepository, Error noAssessorProcessRole) {

        Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
        return matchingRole.map(ServiceResult::serviceSuccess).orElse(serviceFailure(noAssessorProcessRole));
    }

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
