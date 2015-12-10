package com.worth.ifs.util;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
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

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.toSuppliedLeft;
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
     * @param httpResponse
     * @param serviceLocator
     * @return
     */
    public static Either<JsonStatusResponse, JsonStatusResponse> withProcessRoleReturnJsonResponse(Long userId, UserRoleType roleType, Long applicationId, HttpServletResponse httpResponse,
                                                                       ServiceLocator serviceLocator, Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>> doWithProcessRoleFn) {

        Supplier<JsonStatusResponse> noRoleAvailable = () -> JsonStatusResponse.badRequest("No role of type " + roleType + " set up on Application " + applicationId, httpResponse);
        Supplier<JsonStatusResponse> noProcessRoleAvailable = () -> JsonStatusResponse.badRequest("No process role of type " + roleType + " set up on Application " + applicationId, httpResponse);

        return getRoleForRoleType(roleType, serviceLocator.getRoleRepository(), noRoleAvailable)
            .map(role -> getProcessRoleForRoleUserAndApplication(role, userId, applicationId, serviceLocator.getProcessRoleRepository(), noProcessRoleAvailable)
                .map(doWithProcessRoleFn::apply)
            );
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

    public static <FailureType> Either<FailureType, ProcessRole> getProcessRoleById(Long processRoleId,
                                                                                    ProcessRoleRepository processRoleRepository,
                                                                                    Supplier<FailureType> noProcessRole) {

        return ofNullable(processRoleRepository.findOne(processRoleId)).
                map(Either::<FailureType, ProcessRole> right).
                orElse(left(noProcessRole.get()));
    }

    public static <FailureType> Either<FailureType, Response> getResponseById(Long responseId,
                                                                          ResponseRepository responseRepository,
                                                                          Supplier<FailureType> noResponse) {

        return ofNullable(responseRepository.findOne(responseId)).
                map(Either::<FailureType, Response> right).
                orElse(left(noResponse.get()));
    }
}
