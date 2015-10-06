package com.worth.ifs.util;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.IfsFunctionUtils.ifPresent;

/**
 * Created by dwatson on 05/10/15.
 */
public class IfsCommonFunctions {

    public static <FailureType> Function<UserRoleType, Either<FailureType, Role>> getRoleForRoleTypeFn(RoleRepository roleRepository, Supplier<FailureType> noAssessorRoleOnApplication) {
        return type -> {
            Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
            return ifPresent(matchingRole, Either::<FailureType, Role> right).orElse(left(noAssessorRoleOnApplication.get()));
        };
    }

    public static <FailureType> Function<Role, Either<FailureType, ProcessRole>> getProcessRoleForRoleUserAndApplication(Long userId, Long applicationId, ProcessRoleRepository processRoleRepository,
                                                                                                                         Supplier<FailureType> noAssessorProcessRole) {
        return role -> {
            Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
            return ifPresent(matchingRole, Either::<FailureType, ProcessRole> right).orElse(left(noAssessorProcessRole.get()));
        };
    }
}
