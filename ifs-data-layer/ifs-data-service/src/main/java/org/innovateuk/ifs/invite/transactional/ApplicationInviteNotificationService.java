package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteServiceImpl.Notifications;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;

/**
 * A component for handling the sending of notifications from the ApplicationInviteService
 */
@Component
class ApplicationInviteNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    private LocalValidatorFactoryBean validator;

    public ApplicationInviteNotificationService() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Transactional
    ServiceResult<Void> inviteCollaborators(List<ApplicationInvite> invites) {

        for (ApplicationInvite invite : invites) {
            ServiceResult<Void> inviteResult = processCollaboratorInvite(webBaseUrl, invite);

            if (inviteResult.isFailure()) {
                return inviteResult;
            }
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> processCollaboratorInvite(String baseUrl, ApplicationInvite invite) {
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        validator.validate(invite, errors);

        if (errors.hasErrors()) {
            return serviceFailure(internalServerErrorError());
        } else {
            if (invite.getId() == null) {
                applicationInviteRepository.save(invite);
            }
            invite.setHash(generateInviteHash());
            applicationInviteRepository.save(invite);
            return inviteCollaboratorToApplication(baseUrl, invite).
                    andOnSuccessReturnVoid(() -> handleInviteSuccess(invite));
        }
    }

    private ServiceResult<Void> inviteCollaboratorToApplication(String baseUrl, ApplicationInvite invite) {

        User loggedInUser = loggedInUserSupplier.get();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(invite.getName(), invite.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        if (StringUtils.isNotEmpty(invite.getTarget().getName())) {
            notificationArguments.put("applicationName", invite.getTarget().getName());
        }
        notificationArguments.put("sentByName", loggedInUser.getName());
        notificationArguments.put("applicationId", invite.getTarget().getId());
        notificationArguments.put("competitionName", invite.getTarget().getCompetition().getName());
        notificationArguments.put("competitionUrl", getCompetitionDetailsUrl(baseUrl, invite));
        notificationArguments.put("inviteUrl", getInviteUrl(baseUrl, invite));
        if (invite.getInviteOrganisation().getOrganisation() != null) {
            notificationArguments.put("inviteOrganisationName", invite.getInviteOrganisation().getOrganisation().getName());
        } else {
            notificationArguments.put("inviteOrganisationName", invite.getInviteOrganisation().getOrganisationName());
        }
        ProcessRole leadRole = invite.getTarget().getLeadApplicantProcessRole();
        Organisation organisation = organisationRepository.findOne(leadRole.getOrganisationId());
        notificationArguments.put("leadOrganisation", organisation.getName());
        notificationArguments.put("leadApplicant", invite.getTarget().getLeadApplicant().getName());

        if (invite.getTarget().getLeadApplicant().getTitle() != null) {
            notificationArguments.put("leadApplicantTitle", invite.getTarget().getLeadApplicant().getTitle());
        } else {
            notificationArguments.put("leadApplicantTitle", "");
        }

        Notification notification = new Notification(from, singletonList(to), Notifications.INVITE_COLLABORATOR, notificationArguments);
        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private String getInviteUrl(String baseUrl, ApplicationInvite invite) {
        return format("%s/accept-invite/%s", baseUrl, invite.getHash());
    }

    private String getCompetitionDetailsUrl(String baseUrl, ApplicationInvite invite) {
        return baseUrl + "/competition/" + invite.getTarget().getCompetition().getId() + "/overview";
    }

    private void handleInviteSuccess(ApplicationInvite invite) {
        applicationInviteRepository.save(invite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }
}
