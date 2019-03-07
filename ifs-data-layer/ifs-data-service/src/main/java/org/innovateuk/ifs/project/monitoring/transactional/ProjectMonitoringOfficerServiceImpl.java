package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.ProjectMonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.ProjectMonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectMonitoringOfficerServiceImpl implements ProjectMonitoringOfficerService {

    private static final Log LOG = LogFactory.getLog(MonitoringOfficerInviteService.class);

    // TODO move autowireds to constructor

    @Autowired
    private ProjectMonitoringOfficerRepository projectMonitoringOfficerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationService organisationService;

    @Override
    @Transactional
    public ServiceResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long userId) {
        return getMonitoringOfficerUser(userId)
                .andOnSuccess(user -> getAssignedProjects(userId)
                    .andOnSuccess(assignedProjects -> getUnassignedProjects()
                        .andOnSuccessReturn(unassignedProjects -> new ProjectMonitoringOfficerResource(
                                userId, user.getFirstName(), user.getLastName(), unassignedProjects, assignedProjects
                        ))
                    )
                );
    }


    @Override
    @Transactional
    public ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId) {
        return getMonitoringOfficerUser(userId)
               .andOnSuccess(user -> getProject(projectId)
                       .andOnSuccessReturnVoid(project ->
                               projectMonitoringOfficerRepository.save(new ProjectMonitoringOfficer(user, project))
                       )
               );
    }

    @Override
    @Transactional
    public ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId) {
        projectMonitoringOfficerRepository.deleteByUserIdAndProjectId(userId, projectId);
        return serviceSuccess();
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

    // TODO suspect we're mixing up project ids and application ids quite a bit

    private ServiceResult<MonitoringOfficerUnassignedProjectResource> mapToUnassignedProject(Project project) {
        return serviceSuccess(new MonitoringOfficerUnassignedProjectResource(project.getId(), project.getName()));
    }

    private ServiceResult<OrganisationResource> getLeadOrganisationForProject(Project project) {
        return organisationService.findById(project.getApplication().getLeadOrganisationId());
    }

}