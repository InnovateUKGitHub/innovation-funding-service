package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * Service that encompasses functions that relate to users and their roles
 */
@Service
public class UsersRolesServiceImpl extends BaseTransactionalService implements UsersRolesService {

    @Override
    public ServiceResult<ProcessRole> getProcessRoleById(Long id) {
        return super.getProcessRole(id);
    }

    @Override
    public ServiceResult<List<ProcessRole>> getProcessRolesByApplicationId(Long applicationId) {
        return getOrFail(() -> processRoleRepository.findByApplicationId(applicationId), notFoundError(ProcessRole.class, "Application", applicationId));
    }

    @Override
    public ServiceResult<ProcessRole> getProcessRoleByUserIdAndApplicationId(Long userId, Long applicationId) {
        return getOrFail(() -> processRoleRepository.findByUserIdAndApplicationId(userId, applicationId), notFoundError(ProcessRole.class, "User", userId, "Application", applicationId));
    }

    @Override
    public ServiceResult<List<ProcessRole>> getProcessRolesByUserId(Long userId) {
        return getOrFail(() -> processRoleRepository.findByUserId(userId), notFoundError(ProcessRole.class, "User", userId));
    }

    @Override
    public ServiceResult<List<ProcessRole>> getAssignableProcessRolesByApplicationId(Long applicationId) {

        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(applicationId);
        Set<ProcessRole> assignableProcessRoles = processRoles.stream()
                .filter(r -> LEADAPPLICANT.getName().equals(r.getRole().getName()) ||
                        COLLABORATOR.getName().equals(r.getRole().getName()))
                .collect(Collectors.toSet());
        return serviceSuccess(new ArrayList<>(assignableProcessRoles));
    }
}
