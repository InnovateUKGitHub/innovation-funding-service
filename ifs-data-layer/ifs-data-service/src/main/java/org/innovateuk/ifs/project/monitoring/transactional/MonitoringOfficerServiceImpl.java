package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class MonitoringOfficerServiceImpl implements MonitoringOfficerService {

    private static final Log LOG = LogFactory.getLog(MonitoringOfficerInviteService.class);

    private MonitoringOfficerRepository monitoringOfficerRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private OrganisationService organisationService;
    private ProjectMapper projectMapper;

    public MonitoringOfficerServiceImpl(MonitoringOfficerRepository monitoringOfficerRepository,
                                        ProjectRepository projectRepository,
                                        UserRepository userRepository,
                                        OrganisationService organisationService,
                                        ProjectMapper projectMapper) {
        this.monitoringOfficerRepository = monitoringOfficerRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.organisationService = organisationService;
        this.projectMapper = projectMapper;
    }

    @Override
    public ServiceResult<List<MonitoringOfficerResource>> findAll() {
        return find(userRepository.findByRoles(MONITORING_OFFICER), notFoundError(User.class))
                .andOnSuccessReturn(userList -> simpleMap(userList,
                                                          user -> mapToProjectMonitoringOfficerResource(user).getSuccess()
                                    )
                );
    }

    private ServiceResult<MonitoringOfficerResource> mapToProjectMonitoringOfficerResource(User user) {
        return getAssignedProjects(user.getId())
                .andOnSuccess(assignedProjects -> getUnassignedProjects()
                        .andOnSuccessReturn(unassignedProjects ->
                                                    new MonitoringOfficerResource(user.getId(),
                                                                                         user.getFirstName(),
                                                                                         user.getLastName(),
                                                                                         unassignedProjects,
                                                                                         assignedProjects)
                        )
                );
    }

    @Override
    @Transactional
    public ServiceResult<MonitoringOfficerResource> getProjectMonitoringOfficer(long userId) {
        return getMonitoringOfficerUser(userId)
                .andOnSuccess(this::mapToProjectMonitoringOfficerResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId) {
        return getMonitoringOfficerUser(userId)
               .andOnSuccess(user -> getProject(projectId)
                       .andOnSuccessReturnVoid(project ->
                               monitoringOfficerRepository.save(new MonitoringOfficer(user, project))
                       )
               );
    }

    @Override
    @Transactional
    public ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId) {
        monitoringOfficerRepository.deleteByUserIdAndProjectId(userId, projectId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ProjectResource>> getMonitoringOfficerProjects(long userId) {
        List<MonitoringOfficer> monitoringOfficers = monitoringOfficerRepository.findByUserId(userId);
        return serviceSuccess(monitoringOfficers.stream()
                .map(MonitoringOfficer::getProcess)
                .map(projectMapper::mapToResource)
                .collect(toList()));
    }

    private ServiceResult<User> getMonitoringOfficerUser(long userId) {
        return find(userRepository.findByIdAndRoles(userId, MONITORING_OFFICER), notFoundError(User.class, userId));
    }

    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<List<MonitoringOfficerAssignedProjectResource>> getAssignedProjects(long userId) {
        return ServiceResult.aggregate(simpleMap(projectRepository.findAssigned(userId), this::mapToAssignedProject));
    }

    private ServiceResult<List<MonitoringOfficerUnassignedProjectResource>> getUnassignedProjects() {
        return ServiceResult.aggregate(simpleMap(projectRepository.findAssignable(), this::mapToUnassignedProject));
    }

    private ServiceResult<MonitoringOfficerAssignedProjectResource> mapToAssignedProject(Project project) {
        return getLeadOrganisationForProject(project)
                .andOnSuccessReturn(leadOrg -> new MonitoringOfficerAssignedProjectResource(
                        project.getId(),
                        project.getApplication().getId(),
                        project.getApplication().getCompetition().getId(),
                        project.getName(),
                        leadOrg.getName())
        );
    }

    private ServiceResult<MonitoringOfficerUnassignedProjectResource> mapToUnassignedProject(Project project) {
        return serviceSuccess(new MonitoringOfficerUnassignedProjectResource(project.getId(), project.getApplication().getId(), project.getName()));
    }

    private ServiceResult<OrganisationResource> getLeadOrganisationForProject(Project project) {
        return organisationService.findById(project.getApplication().getLeadOrganisationId());
    }
}