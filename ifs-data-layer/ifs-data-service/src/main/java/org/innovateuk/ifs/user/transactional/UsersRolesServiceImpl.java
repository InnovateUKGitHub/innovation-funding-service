package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
        return find(processRoleRepository.findByUserIdAndApplicationId(userId, applicationId), notFoundError(ProcessRole.class, "User", userId, "Application", applicationId))
                .andOnSuccessReturn(processRoleMapper::mapToResource);
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

    @Override
    public ServiceResult<Boolean> userHasApplicationForCompetition(Long userId, Long competitionId) {
        return serviceSuccess(applicationRepository.countByProcessRolesUserIdAndCompetitionId(userId, competitionId) > 0);
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
