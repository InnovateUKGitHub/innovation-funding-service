package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.user.resource.Role.LIVE_PROJECTS_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);

    /**
     * Feature flag to allow early release of the new multi-role dashboard without giving access to Live Projects
     * immediately.
     **/
    @Value("${ifs.data.service.allocate.live.projects.role}")
    private boolean allocateLiveProjectsRole;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GrantProcessService grantProcessService;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Autowired
    private GrantMapper grantMapper;

    private static final List<ProjectParticipantRole> LIVE_PROJECT_ACCESS_ROLES =
            asList(
                    PROJECT_MANAGER,
                    PROJECT_FINANCE_CONTACT
            );

    @Override
    @Transactional
    public ServiceResult<Void> sendReadyProjects() {
        List<GrantProcess> readyToSend = grantProcessService.findReadyToSend();
        LOG.debug("Sending " + readyToSend.size() + " projects");
        readyToSend.forEach(this::sendProject);
        return serviceSuccess();
    }

    private ServiceResult<Void> sendProject(GrantProcess grantProcess) {
        long applicationId = grantProcess.getApplicationId();
        LOG.info("Sending project : " + applicationId);

        Grant grant = grantMapper.mapToGrant(
                projectRepository.findOneByApplicationId(applicationId)
        );

        grantEndpoint.send(grant)
                .andOnSuccess(() -> grantProcessService.sendSucceeded(applicationId))
                .andOnSuccess(() -> addLiveProjectsRoleToUsers(applicationId))
                .andOnFailure((ServiceFailure serviceFailure) ->
                        grantProcessService.sendFailed(applicationId, serviceFailure.toDisplayString()));

        return serviceSuccess();
    }

    private ServiceResult<Void> addLiveProjectsRoleToUsers(long applicationId) {
        return !allocateLiveProjectsRole ?
                serviceSuccess() : addLiveProjectsRoleToProjectTeamUsers(projectRepository.findOneByApplicationId(applicationId));
    }

    private ServiceResult<Void> addLiveProjectsRoleToProjectTeamUsers(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> liveProjectAccessUsers = simpleFilter(projectUsers,
                projectUser -> LIVE_PROJECT_ACCESS_ROLES.contains(projectUser.getRole()));

        liveProjectAccessUsers.forEach(projectUser -> {

            User user = projectUser.getUser();

            if (!user.hasRole(LIVE_PROJECTS_USER)) {
                user.addRole(LIVE_PROJECTS_USER);
            }
        });

        return serviceSuccess();
    }
}