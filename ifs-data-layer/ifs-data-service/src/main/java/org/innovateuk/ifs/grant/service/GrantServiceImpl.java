package org.innovateuk.ifs.grant.service;

import com.newrelic.api.agent.Trace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.user.resource.Role.LIVE_PROJECTS_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GrantProcessService grantProcessService;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Autowired
    private GrantMapper grantMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CrmService crmService;

    private static final List<ProjectParticipantRole> LIVE_PROJECT_ACCESS_ROLES =
            asList(
                    PROJECT_MANAGER,
                    PROJECT_FINANCE_CONTACT
            );

    @Override
    @Transactional
    @Trace(dispatcher = true)
    public ServiceResult<ScheduleResponse> sendReadyProjects() {
        return grantProcessService.findOneReadyToSend()
                .map(this::sendProject)
                .orElse(serviceSuccess(ScheduleResponse.noWorkNeeded()));
    }

    private ServiceResult<ScheduleResponse> sendProject(GrantProcess grantProcess) {
        long applicationId = grantProcess.getApplicationId();
        LOG.info("Sending project : " + applicationId);

        Project project = projectRepository.findOneByApplicationId(applicationId);

        Grant grant = grantMapper.mapToGrant(project);

        ServiceResult<Void> syncParticipantsResult = syncParticipants(grant, project);

        ScheduleResponse scheduleResponse;
        if (syncParticipantsResult.isSuccess()) {
            grantEndpoint.send(grant)
                    .andOnSuccess(() -> grantProcessService.sendSucceeded(applicationId))
                    .andOnSuccess(() -> addLiveProjectsRoleToProjectTeamUsers(projectRepository.findOneByApplicationId(applicationId)))
                    .andOnFailure((ServiceFailure serviceFailure) ->
                            grantProcessService.sendFailed(applicationId, serviceFailure.toDisplayString()));
            scheduleResponse = new ScheduleResponse("Project sent: " + applicationId);
        } else {
            grantProcessService.sendFailed(applicationId, syncParticipantsResult.getFailure().toDisplayString());
            scheduleResponse = new ScheduleResponse("Project send failed: " + applicationId);
        }

        return serviceSuccess(scheduleResponse);
    }

    private ServiceResult<Void> syncParticipants(Grant grant, Project project) {
        ServiceResult<Void> syncCrmContactResults = serviceSuccess();

        for (Participant participant : grant.getParticipants()) {
            syncCrmContactResults =  syncCrmContactResults
                    .andOnSuccess(() -> crmService.syncCrmContact(participant.getContactId(), project.getId()));
        }

        return syncCrmContactResults;
    }

    private ServiceResult<Void> addLiveProjectsRoleToProjectTeamUsers(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> liveProjectAccessUsers = simpleFilter(projectUsers,
                projectUser -> LIVE_PROJECT_ACCESS_ROLES.contains(projectUser.getRole()));

        liveProjectAccessUsers.forEach(projectUser -> {
            User user = projectUser.getUser();
            addLiveRole(user);
        });

        project.getProjectMonitoringOfficer().ifPresent(mo -> addLiveRole(mo.getUser()));

        return serviceSuccess();
    }

    private void addLiveRole(User user) {
        if(!user.hasRole(LIVE_PROJECTS_USER)) {
            user.addRole(LIVE_PROJECTS_USER);
            userService.evictUserCache(user.getUid());
        }
    }
}