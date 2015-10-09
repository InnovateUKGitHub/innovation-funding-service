package com.worth.ifs.util;

import com.worth.ifs.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.getEither;
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

        return getRoleForRoleTypeFn(roleType, serviceLocator.getRoleRepository(), noRoleAvailable).andThen(role -> {
            return getProcessRoleForRoleUserAndApplication(role, userId, applicationId, serviceLocator.getProcessRoleRepository(), noProcessRoleAvailableForApplicationAndUser).
                    andThen(doWithProcessRoleFn);
        });
    }

    public static Function<Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>>, JsonStatusResponse> withProcessRoleReturnJsonResponse(Long userId, UserRoleType roleType, Long applicationId, HttpServletResponse httpResponse,
                                                                       ServiceLocator serviceLocator) {

        return processRole -> getEither(doWithProcessRole(userId, roleType, applicationId, serviceLocator, httpResponse, processRole,
                () -> JsonStatusResponse.badRequest("No process role of type " + roleType + " set up on Application " + applicationId, httpResponse),
                () -> JsonStatusResponse.badRequest("No role of type " + roleType + " set up on Application " + applicationId, httpResponse)));
    }

}
