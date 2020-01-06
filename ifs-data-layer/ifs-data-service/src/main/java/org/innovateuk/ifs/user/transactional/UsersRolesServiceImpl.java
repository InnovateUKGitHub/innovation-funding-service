package org.innovateuk.ifs.user.transactional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that encompasses functions that relate to users and their roles
 */
@Service
public class UsersRolesServiceImpl extends BaseTransactionalService implements UsersRolesService {

    @Autowired
    private ProcessRoleMapper processRoleMapper;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Override
    public ServiceResult<ProcessRoleResource> getProcessRoleById(long id) {
        return super.getProcessRole(id).andOnSuccessReturn(processRoleMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByIds(Long[] ids) {
        List<Long> processRoleIds = asList(ids);
        return serviceSuccess(processRolesToResources(processRoleRepository.findAllById(processRoleIds)));
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(long applicationId) {
        return serviceSuccess(processRolesToResources(processRoleRepository.findByApplicationId(applicationId)));
    }

    @Override
    public ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(long userId, long applicationId) {
        return find(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(userId, applicantProcessRoles(), applicationId), notFoundError(ProcessRole.class, "User", userId, "Application", applicationId))
                .andOnSuccessReturn(processRoleMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(long userId) {
        return serviceSuccess(processRolesToResources(processRoleRepository.findByUserId(userId)));
    }

    @Override
    public ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(long applicationId) {
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(applicationId);

        Set<ProcessRoleResource> assignableProcessRoleResources = processRoles.stream()
                .filter(r -> LEADAPPLICANT == r.getRole() ||
                        COLLABORATOR == r.getRole())
                .map(processRoleMapper::mapToResource)
                .collect(Collectors.toSet());

        return serviceSuccess(new ArrayList<>(assignableProcessRoleResources));
    }

    @Override
    public ServiceResult<Boolean> userHasApplicationForCompetition(long userId, long competitionId) {
        return serviceSuccess(applicationRepository.existsByProcessRolesUserIdAndCompetitionId(userId, competitionId));
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
