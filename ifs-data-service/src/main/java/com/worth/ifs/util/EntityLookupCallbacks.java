package com.worth.ifs.util;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
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

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.PROCESS_ROLE_NOT_FOUND;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.ROLE_NOT_FOUND;
import static com.worth.ifs.transactional.ServiceResult.failureSupplier;
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

        return getRoleForRoleType(roleType, serviceLocator.getRoleRepository(), ROLE_NOT_FOUND)
                .map(role -> getProcessRoleForRoleUserAndApplication(role, userId, applicationId,serviceLocator.getProcessRoleRepository(), PROCESS_ROLE_NOT_FOUND)
                .map(doWithProcessRoleFn::apply));
    }

    public static ServiceResult<Role> getRoleForRoleType(UserRoleType type,
             RoleRepository roleRepository, Enum<?> noRoleFound) {

        Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
        return matchingRole.map(ServiceResult::success).orElseGet(failureSupplier(noRoleFound));
    }

    public static ServiceResult<ProcessRole> getProcessRoleForRoleUserAndApplication(Role role, Long userId,
            Long applicationId, ProcessRoleRepository processRoleRepository,
            Enum<?> noAssessorProcessRole) {

        Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
        return matchingRole.map(ServiceResult::success).orElseGet(failureSupplier(noAssessorProcessRole));
    }

    public static ServiceResult<ProcessRole> getProcessRoleById(Long processRoleId,
            ProcessRoleRepository processRoleRepository,
            Enum<?> noProcessRole) {

        return getOrFail(() -> processRoleRepository.findOne(processRoleId), noProcessRole);
    }

    public static ServiceResult<Response> getResponseById(Long responseId,
                                                          ResponseRepository responseRepository,
                                                          Enum<?> noResponse) {

        return getOrFail(() -> responseRepository.findOne(responseId), noResponse);
    }

    public static <SuccessType> ServiceResult<SuccessType> getOrFail(
            Supplier<SuccessType> getterFn,
            Enum<?> failureResponse) {

        return ofNullable(getterFn.get()).
                map(ServiceResult::success).
                orElse(ServiceResult.failure(failureResponse));
    }
}
