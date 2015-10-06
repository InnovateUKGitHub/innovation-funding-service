package com.worth.ifs.util;

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

import static com.worth.ifs.util.IfsFunctions.Either.toLeft;
import static com.worth.ifs.util.IfsFunctions.FunctionChains.chained;
import static com.worth.ifs.util.IfsFunctions.getEither;
import static com.worth.ifs.util.IfsFunctions.ifPresent;

/**
 * Created by dwatson on 05/10/15.
 */
public class IfsCommonTasks {

    private static final Log log = LogFactory.getLog(IfsCommonTasks.class);

    public static <FailureType, SuccessType> IfsFunctions.Either<FailureType, SuccessType> doWithProcessRole(Long userId, UserRoleType roleType, Long applicationId, RoleRepository roleRepository, ProcessRoleRepository processRoleRepository, HttpServletResponse httpResponse, Function<ProcessRole, IfsFunctions.Either<FailureType, SuccessType>> doWithProcessRoleFn,
                                                                                                             Supplier<FailureType> noAssessorProcessRole, Supplier<FailureType> noAssessorRoleOnApplication) {

        Optional<Role> getRole = roleRepository.findByName(roleType.getName()).stream().findFirst();

        Function<UserRoleType, IfsFunctions.Either<FailureType, Role>> getRoleForUserRoleType = type -> {
            Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
            return ifPresent(matchingRole, IfsFunctions.Either::<FailureType, Role> right).orElse(toLeft(noAssessorRoleOnApplication.get()));
        };

        Function<Role, IfsFunctions.Either<FailureType, ProcessRole>> getProcessRoleForUserRoleAndApplication = role -> {
            Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
            return ifPresent(matchingRole, IfsFunctions.Either::<FailureType, ProcessRole> right).orElse(toLeft(noAssessorProcessRole.get()));
        };

        return getRoleForUserRoleType.andThen(chained(getProcessRoleForUserRoleAndApplication)).andThen(chained(doWithProcessRoleFn)).apply(roleType);
    }

    public static JsonStatusResponse jsonResponseWithProcessRole(Long userId, UserRoleType roleType, Long applicationId, RoleRepository roleRepository, ProcessRoleRepository processRoleRepository, HttpServletResponse httpResponse, Function<ProcessRole, IfsFunctions.Either<JsonStatusResponse, JsonStatusResponse>> doWithProcessRoleFn) {

        return getEither(doWithProcessRole(userId, roleType, applicationId, roleRepository, processRoleRepository, httpResponse, doWithProcessRoleFn,
                () -> JsonStatusResponse.badRequest("No process role of type " + roleType + " set up on Application " + applicationId, httpResponse),
                () -> JsonStatusResponse.badRequest("No role of type " + roleType + " set up on Application " + applicationId, httpResponse)));
    }

}
