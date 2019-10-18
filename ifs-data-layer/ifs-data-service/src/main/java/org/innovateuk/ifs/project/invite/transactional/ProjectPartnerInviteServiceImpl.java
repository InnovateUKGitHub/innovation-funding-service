package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectPartnerInviteServiceImpl extends RootTransactionalService implements ProjectPartnerInviteService {

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ProjectInviteValidator projectInviteValidator;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_PROJECT_PARTNER_ORGANISATION
    }

    @Override
    @Transactional
    public ServiceResult<Void> invitePartnerOrganisation(long projectId, SendProjectPartnerInviteResource invite) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId)).andOnSuccess(project ->
                projectInviteValidator.validate(projectId, invite).andOnSuccess(() -> {
                    InviteOrganisation inviteOrganisation = new InviteOrganisation();
                    inviteOrganisation.setOrganisationName(invite.getOrganisationName());
                    inviteOrganisation = inviteOrganisationRepository.save(inviteOrganisation);

                    ProjectPartnerInvite projectPartnerInvite = new ProjectPartnerInvite();
                    projectPartnerInvite.setInviteOrganisation(inviteOrganisation);
                    projectPartnerInvite.setEmail(invite.getEmail());
                    projectPartnerInvite.setName(invite.getUserName());
                    projectPartnerInvite.setHash(generateInviteHash());
                    projectPartnerInvite.setTarget(project);

                    projectPartnerInvite = projectPartnerInviteRepository.save(projectPartnerInvite);
                    return sendInviteNotification(projectPartnerInvite)
                            .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
                })
        );
    }

    private ServiceResult<ProjectPartnerInvite> sendInviteNotification(ProjectPartnerInvite projectPartnerInvite) {
        return find(projectPartnerInvite.getTarget().getLeadOrganisation(), notFoundError(Organisation.class)).andOnSuccess(leadOrganisation -> {
            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new UserNotificationTarget(projectPartnerInvite.getName(), projectPartnerInvite.getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("inviteUrl", webBaseUrl);
            notificationArguments.put("applicationId", projectPartnerInvite.getTarget().getApplication().getId());
            notificationArguments.put("projectName", projectPartnerInvite.getTarget().getName());
            notificationArguments.put("leadOrganisationName", leadOrganisation.getOrganisation().getName());

            Notification notification = new Notification(from, singletonList(to), Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);

            return notificationService.sendNotificationWithFlush(notification, EMAIL)
                    .andOnSuccessReturn(() -> projectPartnerInvite);
        });
    }

    @Override
    public ServiceResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId) {
        return serviceSuccess(projectPartnerInviteRepository.findByProjectId(projectId).stream()
            .map(this::mapToSentResource)
            .collect(toList()));
    }

    private SentProjectPartnerInviteResource mapToSentResource(ProjectPartnerInvite projectPartnerInvite) {
        return new SentProjectPartnerInviteResource(projectPartnerInvite.getId(), projectPartnerInvite.getSentOn(),
                projectPartnerInvite.getInviteOrganisation().getOrganisationName(), projectPartnerInvite.getName(), projectPartnerInvite.getEmail());
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(projectPartnerInviteRepository.findById(inviteId), notFoundError(ProjectPartnerInvite.class, inviteId))
                .andOnSuccess(this::sendInviteNotification)
                .andOnSuccessReturnVoid((sentInvite) -> sentInvite.resend(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    @Override
    public ServiceResult<Void> deleteInvite(long inviteId) {
        return find(projectPartnerInviteRepository.findById(inviteId), notFoundError(ProjectPartnerInvite.class, inviteId))
                .andOnSuccessReturnVoid(projectPartnerInviteRepository::delete);
    }
}
