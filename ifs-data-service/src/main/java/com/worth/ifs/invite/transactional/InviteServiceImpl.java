package com.worth.ifs.invite.transactional;

import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.*;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static java.util.Collections.singletonList;

@Service
public class InviteServiceImpl extends BaseTransactionalService implements InviteService {

    private final Log log = LogFactory.getLog(getClass());

    enum Notifications {
        INVITE_COLLABORATOR
    }

    @Autowired
    ApplicationService applicationService;

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    LocalValidatorFactoryBean validator;

    public InviteServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Override
    public Optional<InviteResource> getInviteByHash(String hash){
        Optional<Invite> invite = inviteRepository.getByHash(hash);
        if(invite.isPresent()){
            return Optional.ofNullable(new InviteResource(invite.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites) {
        List<ServiceResult<Notification>> results = new ArrayList<>();
        invites.stream().forEach(invite -> {
            Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
            validator.validate(invite, errors);

            if(errors.hasErrors()){
                errors.getFieldErrors().stream().peek(e -> log.debug(String.format("Field error: %s ", e.getField())));
                ServiceResult<Notification> inviteResult = serviceFailure(internalServerErrorError("Validation errors"));

                results.add(inviteResult);
                inviteResult.handleFailureOrSuccess(
                        failure -> handleInviteError(invite, failure),
                        success -> handleInviteSuccess(invite)
                );
            }else{
                invite.generateHash();
                inviteRepository.save(invite);

                ServiceResult<Notification> inviteResult = inviteCollaboratorToApplication(baseUrl, invite);

                results.add(inviteResult);
                inviteResult.handleFailureOrSuccess(
                        failure -> handleInviteError(invite, failure),
                        success -> handleInviteSuccess(invite)
                );
            }
        });
        return results;
    }

    private boolean handleInviteSuccess(Invite i) {
        i.setStatus(InviteStatusConstants.SEND);
        inviteRepository.save(i);
        return true;
    }

    private boolean handleInviteError(Invite i, ServiceFailure failure) {
        log.error(String.format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
        return true;
    }

    private String getInviteUrl(String baseUrl, Invite invite) {
        return String.format("%s/accept-invite/%s", baseUrl, invite.getHash());
    }

    @Override
    public ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite) {
        log.warn("inviteCollaboratorToApplication");
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationName", invite.getApplication().getName());
        notificationArguments.put("inviteUrl", getInviteUrl(baseUrl, invite));
        notificationArguments.put("leadOrganisation", invite.getApplication().getLeadOrganisation().get().getName());
        notificationArguments.put("leadApplicant", invite.getApplication().getLeadApplicant().get().getName());
        notificationArguments.put("leadApplicantEmail", invite.getApplication().getLeadApplicant().get().getEmail());

        Notification notification = new Notification(from, singletonList(to), Notifications.INVITE_COLLABORATOR, notificationArguments);
        return notificationService.sendNotification(notification, EMAIL);

    }

}
