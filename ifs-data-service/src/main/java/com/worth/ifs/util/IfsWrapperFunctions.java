package com.worth.ifs.util;

import com.worth.ifs.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.getEither;
import static com.worth.ifs.util.IfsFunctionUtils.FunctionChains.inChain;
import static com.worth.ifs.util.IfsCommonFunctions.getProcessRoleForRoleUserAndApplication;
import static com.worth.ifs.util.IfsCommonFunctions.getRoleForRoleTypeFn;

/**
 * Created by dwatson on 05/10/15.
 */
public class IfsWrapperFunctions {

    private static final Log log = LogFactory.getLog(IfsWrapperFunctions.class);

    public static <FailureType, SuccessType> Either<FailureType, SuccessType> doWithProcessRole(Long userId, UserRoleType roleType, Long applicationId, ServiceLocator serviceLocator, HttpServletResponse httpResponse,
                                                                                                Function<ProcessRole, Either<FailureType, SuccessType>> doWithProcessRoleFn,
                                                                                                Supplier<FailureType> noProcessRoleAvailableForApplicationAndUser, Supplier<FailureType> noRoleAvailable) {

        RoleRepository roleRepository = serviceLocator.getRoleRepository();
        ProcessRoleRepository processRoleRepository = serviceLocator.getProcessRoleRepository();

        Function<UserRoleType, Either<FailureType, Role>> getRole = getRoleForRoleTypeFn(roleRepository, noRoleAvailable);
        Function<Role, Either<FailureType, ProcessRole>> getProcessRole =
                getProcessRoleForRoleUserAndApplication(userId, applicationId, processRoleRepository, noProcessRoleAvailableForApplicationAndUser);

        return getRole.andThen(inChain(getProcessRole)).andThen(inChain(doWithProcessRoleFn)).apply(roleType);
    }

    public static Function<Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>>, JsonStatusResponse> withProcessRoleReturnJsonResponse(Long userId, UserRoleType roleType, Long applicationId, HttpServletResponse httpResponse,
                                                                       ServiceLocator serviceLocator) {

        return processRole -> getEither(doWithProcessRole(userId, roleType, applicationId, serviceLocator, httpResponse, processRole,
                () -> JsonStatusResponse.badRequest("No process role of type " + roleType + " set up on Application " + applicationId, httpResponse),
                () -> JsonStatusResponse.badRequest("No role of type " + roleType + " set up on Application " + applicationId, httpResponse)));
    }

}
