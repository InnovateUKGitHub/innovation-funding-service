package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectInviteValidator;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.grants.transactional.GrantsInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class GrantsInviteServiceImpl extends BaseTransactionalService implements GrantsInviteService {

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ProjectInviteValidator projectInviteValidator;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_PROJECT_PARTNER_ORGANISATION
    }

    public abstract InviteRepository<GrantsInvite> getInviteRepository();

    public abstract ProjectParticipantRole getProjectParticipantRole();

    @Override
    public ServiceResult<Void> sendInvite(long projectId, SendProjectPartnerInviteResource invite) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId)).andOnSuccess(project ->
                projectInviteValidator.validate(projectId, invite).andOnSuccess(() -> {
                    InviteOrganisation inviteOrganisation = new InviteOrganisation();
                    inviteOrganisation.setOrganisationName(invite.getOrganisationName());
                    inviteOrganisation = inviteOrganisationRepository.save(inviteOrganisation);

                    GrantsInvite grantsInvite = new GrantsInvite();
                    grantsInvite.setInviteOrganisation(inviteOrganisation);
                    grantsInvite.setEmail(invite.getEmail());
                    grantsInvite.setName(invite.getUserName());
                    grantsInvite.setHash(generateInviteHash());
                    grantsInvite.setTarget(project);

                    grantsInvite = getInviteRepository().save(grantsInvite);
                    return sendInviteNotification(grantsInvite)
                            .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
                })
        );
    }

    private ServiceResult<GrantsInvite> sendInviteNotification(GrantsInvite grantsInvite) {
        return find(grantsInvite.getTarget().getLeadOrganisation(), notFoundError(Organisation.class)).andOnSuccess(leadOrganisation -> {
            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new UserNotificationTarget(grantsInvite.getName(), grantsInvite.getEmail());

            // Change to grants link
            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("inviteUrl", String.format("%s/grants-user/project/%d/finance-contact/%s/accept", webBaseUrl, grantsInvite.getProject().getId(), grantsInvite.getHash()));
            notificationArguments.put("applicationId", grantsInvite.getTarget().getApplication().getId());
            notificationArguments.put("projectName", grantsInvite.getTarget().getName());
            notificationArguments.put("leadOrganisationName", leadOrganisation.getOrganisation().getName());

            Notification notification = new Notification(from, singletonList(to), INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);

            return notificationService.sendNotificationWithFlush(notification, EMAIL)
                    .andOnSuccessReturn(() -> grantsInvite);
        });
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(getInviteRepository().findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(this::sendInviteNotification)
                .andOnSuccessReturnVoid((sentInvite) -> sentInvite.resend(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    @Override
    public ServiceResult<Void> deleteInvite(long inviteId) {
        return find(getInviteRepository().findById(inviteId), notFoundError(ProjectPartnerInvite.class, inviteId))
                .andOnSuccessReturnVoid(getInviteRepository()::delete);
    }

    @Override
    public ServiceResult<SentProjectPartnerInviteResource> getInviteByHash(String hash) {
        return find(getInviteRepository().getByHash(hash), notFoundError(GrantsInvite.class, hash))
                .andOnSuccessReturn(this::mapToSentResource);
    }

    private SentProjectPartnerInviteResource mapToSentResource(GrantsInvite grantsInvite) {
        return new SentProjectPartnerInviteResource(grantsInvite.getId(),
                grantsInvite.getSentOn(),
                grantsInvite.getProject().getName(),
                ofNullable(grantsInvite.getUser()).map(User::getId).orElse(null),
                grantsInvite.getStatus(),
                grantsInvite.getInviteOrganisation().getOrganisationName(),
                grantsInvite.getName(),
                grantsInvite.getEmail(),
                grantsInvite.getProject().getApplication().getId());
    }


    @Override
    public ServiceResult<Void> acceptInvite(long inviteId, long organisationId) {
        return find(getInviteRepository().findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(invite ->
                        find(organisation(organisationId))
                                .andOnSuccess((organisation) -> {
                                    Project project = invite.getProject();
                                    invite.getInviteOrganisation().setOrganisation(organisation);

                                    projectUserRepository.save(new ProjectUser(invite.getUser(), project, getProjectParticipantRole(), organisation));
                                    invite.open();
                                    // maybe an activity log for this
                                    // add some of the project user stuff here
                                    return serviceSuccess();
                                }));
    }
}
