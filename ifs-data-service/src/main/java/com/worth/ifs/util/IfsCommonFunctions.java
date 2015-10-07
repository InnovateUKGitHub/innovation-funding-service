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
import static com.worth.ifs.util.Either.toLeft;
import static com.worth.ifs.util.Either.toSuppliedLeft;
import static com.worth.ifs.util.IfsFunctionUtils.ifPresent;

/**
 * Created by dwatson on 05/10/15.
 */
public class IfsCommonFunctions {

    public static <FailureType> Either<FailureType, Role> getRoleForRoleTypeFn(UserRoleType type, RoleRepository roleRepository, Supplier<FailureType> noAssessorRoleOnApplication) {
            Optional<Role> matchingRole = roleRepository.findByName(type.getName()).stream().findFirst();
            return ifPresent(matchingRole, Either::<FailureType, Role> right).orElseGet(toSuppliedLeft(noAssessorRoleOnApplication));
    }

    public static <FailureType> Either<FailureType, ProcessRole> getProcessRoleForRoleUserAndApplication(Role role, Long userId, Long applicationId, ProcessRoleRepository processRoleRepository,
                                                                                                                         Supplier<FailureType> noAssessorProcessRole) {
        Optional<ProcessRole> matchingRole = processRoleRepository.findByUserIdAndRoleAndApplicationId(userId, role, applicationId).stream().findFirst();
        return ifPresent(matchingRole, Either::<FailureType, ProcessRole> right).orElseGet(toSuppliedLeft(noAssessorProcessRole));
    }
}
