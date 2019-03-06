package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.mapper.MonitoringOfficerInviteMapper;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectMonitoringOfficerServiceImpl implements ProjectMonitoringOfficerService {

    private static final Log LOG = LogFactory.getLog(MonitoringOfficerInviteService.class);


    @Autowired
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private MonitoringOfficerInviteMapper monitoringOfficerInviteMapper;

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
        return getUser(userId)
                .andOnSuccess(user -> getAssignedProjects(userId)
                    .andOnSuccess(assignedProjects -> getUnassignedProjects()
                        .andOnSuccessReturn(unassignedProjects -> new ProjectMonitoringOfficerResource(
                                userId, user.getFirstName(), user.getLastName(), unassignedProjects, assignedProjects
                        ))
                    )
                );
    }

    private ServiceResult<User> getUser(long userId) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId));
    }

    private ServiceResult<List<MonitoringOfficerAssignedProjectResource>> getAssignedProjects(long userId) {
        return ServiceResult.aggregate(simpleMap(projectRepository.findByProjectMonitoringOfficerUserId(userId), this::mapToAssignedProject));
    }

    private ServiceResult<List<MonitoringOfficerUnassignedProjectResource>> getUnassignedProjects() {
        return ServiceResult.aggregate(simpleMap(projectRepository.findByProjectMonitoringOfficerIdIsNull(), this::mapToUnassignedProject));
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