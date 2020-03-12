package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.acc.AccFinanceContactInvite;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.repository.AccFinanceContactInviteRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
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
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ORGANISATION_ALREADY_EXISTS_FOR_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public class AccFinanceContactInviteServiceImpl extends BaseTransactionalService implements AccFinanceContactInviteService {

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private AccFinanceContactInviteRepository accFinanceContactInviteRepository;

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

    @Override
    public ServiceResult<Void> sendInvite(long projectId, SendProjectPartnerInviteResource invite) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId)).andOnSuccess(project ->
                projectInviteValidator.validate(projectId, invite).andOnSuccess(() -> {
                    InviteOrganisation inviteOrganisation = new InviteOrganisation();
                    inviteOrganisation.setOrganisationName(invite.getOrganisationName());
                    inviteOrganisation = inviteOrganisationRepository.save(inviteOrganisation);

                    AccFinanceContactInvite accFinanceContactInvite = new AccFinanceContactInvite();
                    accFinanceContactInvite.setInviteOrganisation(inviteOrganisation);
                    accFinanceContactInvite.setEmail(invite.getEmail());
                    accFinanceContactInvite.setName(invite.getUserName());
                    accFinanceContactInvite.setHash(generateInviteHash());
                    accFinanceContactInvite.setTarget(project);

                    accFinanceContactInvite = accFinanceContactInviteRepository.save(accFinanceContactInvite);
                    return sendInviteNotification(accFinanceContactInvite)
                            .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
                })
        );
    }

    private ServiceResult<AccFinanceContactInvite> sendInviteNotification(AccFinanceContactInvite accFinanceContactInvite) {
        return find(accFinanceContactInvite.getTarget().getLeadOrganisation(), notFoundError(Organisation.class)).andOnSuccess(leadOrganisation -> {
            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new UserNotificationTarget(accFinanceContactInvite.getName(), accFinanceContactInvite.getEmail());

            // Change to acc link
            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("inviteUrl", String.format("%s/acc-user/project/%d/finance-contact/%s/accept", webBaseUrl, accFinanceContactInvite.getProject().getId(), accFinanceContactInvite.getHash()));
            notificationArguments.put("applicationId", accFinanceContactInvite.getTarget().getApplication().getId());
            notificationArguments.put("projectName", accFinanceContactInvite.getTarget().getName());
            notificationArguments.put("leadOrganisationName", leadOrganisation.getOrganisation().getName());

            Notification notification = new Notification(from, singletonList(to), ProjectPartnerInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);

            return notificationService.sendNotificationWithFlush(notification, EMAIL)
                    .andOnSuccessReturn(() -> accFinanceContactInvite);
        });
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(accFinanceContactInviteRepository.findById(inviteId), notFoundError(AccFinanceContactInvite.class, inviteId))
                .andOnSuccess(this::sendInviteNotification)
                .andOnSuccessReturnVoid((sentInvite) -> sentInvite.resend(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    @Override
    public ServiceResult<Void> deleteInvite(long inviteId) {
        return find(accFinanceContactInviteRepository.findById(inviteId), notFoundError(ProjectPartnerInvite.class, inviteId))
                .andOnSuccessReturnVoid(accFinanceContactInviteRepository::delete);
    }

    @Override
    public ServiceResult<SentProjectPartnerInviteResource> getInviteByHash(String hash) {
        return find(projectPartnerInviteRepository.getByHash(hash), notFoundError(ProjectPartnerInvite.class, hash))
                .andOnSuccessReturn(this::mapToSentResource);
    }

    private SentProjectPartnerInviteResource mapToSentResource(ProjectPartnerInvite projectPartnerInvite) {
        return new SentProjectPartnerInviteResource(projectPartnerInvite.getId(),
                projectPartnerInvite.getSentOn(),
                projectPartnerInvite.getProject().getName(),
                ofNullable(projectPartnerInvite.getUser()).map(User::getId).orElse(null),
                projectPartnerInvite.getStatus(),
                projectPartnerInvite.getInviteOrganisation().getOrganisationName(),
                projectPartnerInvite.getName(),
                projectPartnerInvite.getEmail(),
                projectPartnerInvite.getProject().getApplication().getId());
    }


    @Override
    public ServiceResult<Void> acceptInvite(long inviteId, long organisationId) {
        return find(projectPartnerInviteRepository.findById(inviteId), notFoundError(ProjectPartnerInvite.class, inviteId))
                .andOnSuccess(invite ->
                        find(organisation(organisationId))
                                .andOnSuccess((organisation) -> {
                                    Project project = invite.getProject();
                                    invite.getInviteOrganisation().setOrganisation(organisation);

                                    projectUserRepository.save(new ProjectUser(invite.getUser(), project, ProjectParticipantRole.ACC_PROJECT_FINANCE_CONTACT, organisation));
                                    invite.open();
                                    // maybe an activity log for this

                                    return serviceSuccess();
                                }));
    }
}
