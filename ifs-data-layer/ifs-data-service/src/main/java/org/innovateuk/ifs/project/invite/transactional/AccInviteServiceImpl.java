package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.acc.AccInvite;
import org.innovateuk.ifs.commons.service.ServiceResult;
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
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AccInviteServiceImpl extends BaseTransactionalService implements AccInviteService {

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

    public abstract InviteRepository<AccInvite> getInviteRepository();

    public abstract ProjectParticipantRole getProjectParticipantRole();

    @Override
    public ServiceResult<Void> sendInvite(long projectId, SendProjectPartnerInviteResource invite) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId)).andOnSuccess(project ->
                projectInviteValidator.validate(projectId, invite).andOnSuccess(() -> {
                    InviteOrganisation inviteOrganisation = new InviteOrganisation();
                    inviteOrganisation.setOrganisationName(invite.getOrganisationName());
                    inviteOrganisation = inviteOrganisationRepository.save(inviteOrganisation);

                    AccInvite accInvite = new AccInvite();
                    accInvite.setInviteOrganisation(inviteOrganisation);
                    accInvite.setEmail(invite.getEmail());
                    accInvite.setName(invite.getUserName());
                    accInvite.setHash(generateInviteHash());
                    accInvite.setTarget(project);

                    accInvite = getInviteRepository().save(accInvite);
                    return sendInviteNotification(accInvite)
                            .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
                })
        );
    }

    private ServiceResult<AccInvite> sendInviteNotification(AccInvite accInvite) {
        return find(accInvite.getTarget().getLeadOrganisation(), notFoundError(Organisation.class)).andOnSuccess(leadOrganisation -> {
            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new UserNotificationTarget(accInvite.getName(), accInvite.getEmail());

            // Change to acc link
            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("inviteUrl", String.format("%s/acc-user/project/%d/finance-contact/%s/accept", webBaseUrl, accInvite.getProject().getId(), accInvite.getHash()));
            notificationArguments.put("applicationId", accInvite.getTarget().getApplication().getId());
            notificationArguments.put("projectName", accInvite.getTarget().getName());
            notificationArguments.put("leadOrganisationName", leadOrganisation.getOrganisation().getName());

            Notification notification = new Notification(from, singletonList(to), ProjectPartnerInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);

            return notificationService.sendNotificationWithFlush(notification, EMAIL)
                    .andOnSuccessReturn(() -> accInvite);
        });
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(getInviteRepository().findById(inviteId), notFoundError(AccInvite.class, inviteId))
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
        return find(getInviteRepository().getByHash(hash), notFoundError(AccInvite.class, hash))
                .andOnSuccessReturn(this::mapToSentResource);
    }

    private SentProjectPartnerInviteResource mapToSentResource(AccInvite accInvite) {
        return new SentProjectPartnerInviteResource(accInvite.getId(),
                accInvite.getSentOn(),
                accInvite.getProject().getName(),
                ofNullable(accInvite.getUser()).map(User::getId).orElse(null),
                accInvite.getStatus(),
                accInvite.getInviteOrganisation().getOrganisationName(),
                accInvite.getName(),
                accInvite.getEmail(),
                accInvite.getProject().getApplication().getId());
    }


    @Override
    public ServiceResult<Void> acceptInvite(long inviteId, long organisationId) {
        return find(getInviteRepository().findById(inviteId), notFoundError(AccInvite.class, inviteId))
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
