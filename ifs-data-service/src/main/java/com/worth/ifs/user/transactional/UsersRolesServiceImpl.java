package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Arrays.asList;

/**
 * Service that encompasses functions that relate to users and their roles
 */
@Service
public class UsersRolesServiceImpl extends BaseTransactionalService implements UsersRolesService {

    @Autowired
    private ProcessRoleMapper processRoleMapper;

    @Override
    public ServiceResult<ProcessRoleResource> getProcessRoleById(Long id) {
        return super.getProcessRole(id).andOnSuccessReturn(processRoleMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByIds(Long[] ids) {
        List<Long> processRoleIds = asList(ids);
        return serviceSuccess(processRolesToResources(processRoleRepository.findAll(processRoleIds)));
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(Long applicationId) {
        return serviceSuccess(processRolesToResources(processRoleRepository.findByApplicationId(applicationId)));
    }

    @Override
    public ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(Long userId, Long applicationId) {
        return find(processRoleRepository.findByUserIdAndApplicationId(userId, applicationId), notFoundError(ProcessRole.class, "User", userId, "com.worth.ifs.Application", applicationId)).andOnSuccessReturn(processRoleMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(Long userId) {
        return serviceSuccess(processRolesToResources(processRoleRepository.findByUserId(userId)));
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(Long applicationId) {
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(applicationId);

        Set<ProcessRoleResource> assignableProcessRoleResources = processRoles.stream()
                .filter(r -> LEADAPPLICANT.getName().equals(r.getRole().getName()) ||
                        COLLABORATOR.getName().equals(r.getRole().getName()))
                .map(processRoleMapper::mapToResource)
                .collect(Collectors.toSet());

        return serviceSuccess(new ArrayList<>(assignableProcessRoleResources));
    }

    private List<ProcessRoleResource> processRolesToResources(final List<ProcessRole> processRoles) {
        return simpleMap(processRoles, processRoleMapper::mapToResource);
    }

    private List<ProcessRoleResource> processRolesToResources(final Iterable<ProcessRole> processRoles) {
        List<ProcessRoleResource> processRoleResources = new ArrayList<>();
        processRoles.forEach(pr -> processRoleResources.add(processRoleMapper.mapToResource(pr)));
        return processRoleResources;
    }
}
