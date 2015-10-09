package com.worth.ifs.util;

import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.getEither;
import static com.worth.ifs.util.Either.toSuppliedLeft;

/**
 * Utility class to provide common use case wrappers that can be used to wrap more specific pieces of code in a common
 * envelope.  Optionally these wrappers can provide the wrapped piece of code with useful arguments.
 *
 * Created by dwatson on 05/10/15.
 */
public class IfsWrapperFunctions {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(IfsWrapperFunctions.class);

    /**
     * Given a user, a specific Role type and an Application, firstly ensure that the user has the role on the application and
     * if so, call the wrapped code.  Otherwise, fail with the appropriate response.
     *
     * @param userId
     * @param roleType
     * @param applicationId
     * @param serviceLocator
     * @param httpResponse
     * @param doWithProcessRoleFn
     * @param noProcessRoleAvailableForApplicationAndUser
     * @param noRoleAvailable
     * @param <FailureType>
     * @param <SuccessType>
     * @return
     */
    public static <FailureType, SuccessType> Either<FailureType, SuccessType> doWithProcessRole(Long userId, UserRoleType roleType, Long applicationId, ServiceLocator serviceLocator, HttpServletResponse httpResponse,
                                                                                                Function<ProcessRole, Either<FailureType, SuccessType>> doWithProcessRoleFn,
                                                                                                Supplier<FailureType> noProcessRoleAvailableForApplicationAndUser, Supplier<FailureType> noRoleAvailable) {

        return getRoleForRoleType(roleType, serviceLocator.getRoleRepository(), noRoleAvailable).map(role -> {
            return getProcessRoleForRoleUserAndApplication(role, userId, applicationId, serviceLocator.getProcessRoleRepository(), noProcessRoleAvailableForApplicationAndUser).
                    map(doWithProcessRoleFn);
        });
    }

    /**
     * Given a user, a specific Role type and an Application, ensure that the user does indeed have that Role on the Application
     * and if so, call the wrapped code that in turn will either fail or succeed.  If the role is not found for the user and
     * application, return a failure as a JsonStatusResponse.
     *
     * @param userId
     * @param roleType
     * @param applicationId
     * @param httpResponse
     * @param serviceLocator
     * @return
     */
    public static Function<Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>>, JsonStatusResponse> withProcessRoleReturnJsonResponse(Long userId, UserRoleType roleType, Long applicationId, HttpServletResponse httpResponse,
                                                                       ServiceLocator serviceLocator) {

        return processRole -> getEither(doWithProcessRole(userId, roleType, applicationId, serviceLocator, httpResponse, processRole,
                () -> JsonStatusResponse.badRequest("No process role of type " + roleType + " set up on Application " + applicationId, httpResponse),
                () -> JsonStatusResponse.badRequest("No role of type " + roleType + " set up on Application " + applicationId, httpResponse)));
    }

    public static <FailureType> Either<FailureType, Role> getRoleForRoleType(UserRoleType type, RoleRepository roleRepository, Supplier<FailureType> noAssessorRoleOnApplication) {
        Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
        return matchingRole.map(Either::<FailureType, Role>right).orElseGet(toSuppliedLeft(noAssessorRoleOnApplication));
    }

    public static <FailureType> Either<FailureType, ProcessRole> getProcessRoleForRoleUserAndApplication(Role role, Long userId, Long applicationId, ProcessRoleRepository processRoleRepository,
                                                                                                         Supplier<FailureType> noAssessorProcessRole) {
        Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
        return matchingRole.map(Either::<FailureType, ProcessRole> right).orElseGet(toSuppliedLeft(noAssessorProcessRole));
    }
}
