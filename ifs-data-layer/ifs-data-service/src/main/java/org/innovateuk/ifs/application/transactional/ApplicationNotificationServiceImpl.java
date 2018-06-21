package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_INELIGIBLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;


/**
 * Service provides notification emails functions to send emails for {@Application}s.
 */
@Service
public class ApplicationNotificationServiceImpl implements ApplicationNotificationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<Void> notifyApplicantsByCompetition(Long competitionId) {
        List<ProcessRole> applicants = applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId,
                ApplicationSummaryServiceImpl.FUNDING_DECISIONS_MADE_STATUSES)
                .stream()
                .flatMap(x -> x.getProcessRoles().stream())
                .filter(ProcessRole::isLeadApplicantOrCollaborator)
                .collect(toList());

        return processAnyFailuresOrSucceed(applicants
                .stream()
                .map(this::sendNotification)
                .collect(toList()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> informIneligible(long applicationId,
                                                ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    if (!applicationWorkflowHandler.informIneligible(application)) {
                        return serviceFailure(APPLICATION_MUST_BE_INELIGIBLE);
                    }

                    applicationRepository.save(application);
                    String bodyPlain = stripHtml(applicationIneligibleSendResource.getMessage());

                    NotificationTarget recipient = new UserNotificationTarget(
                                    application.getLeadApplicant().getName(),
                                    application.getLeadApplicant().getEmail()
                    );
                    Notification notification = new Notification(
                            systemNotificationSource,
                            singletonList(recipient),
                            Notifications.APPLICATION_INELIGIBLE,
                            asMap("subject", applicationIneligibleSendResource.getSubject(),
                                    "applicationName", application.getName(),
                                    "applicationId", application.getId(),
                                    "competitionName", application.getCompetition().getName(),
                                    "bodyPlain", bodyPlain,
                                    "bodyHtml", applicationIneligibleSendResource.getMessage())
                    );
                    return notificationSender.sendNotification(notification);
                }).andOnSuccessReturnVoid();
    }

    private ServiceResult<List<EmailAddress>> sendNotification(ProcessRole processRole) {
        Application application = applicationRepository.findById(processRole.getApplicationId()).get();

        NotificationTarget recipient =
                new UserNotificationTarget(processRole.getUser().getName(), processRole.getUser().getEmail());

        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                asMap("name", processRole.getUser().getName(),
                        "applicationName", application.getName(),
                        "applicationId", application.getId(),
                        "competitionName", application.getCompetition().getName(),
                        "dashboardUrl", webBaseUrl + "/" + processRole.getRole().getUrl()));

        EmailContent content = notificationSender.renderTemplates(notification).getSuccess().get(recipient);

        return notificationSender.sendEmailWithContent(notification, recipient, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Void> sendNotificationApplicationSubmitted(Long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    NotificationSource from = systemNotificationSource;
                    NotificationTarget to = new UserNotificationTarget(
                            application.getLeadApplicant().getName(),
                            application.getLeadApplicant().getEmail()
                    );

                    Map<String, Object> notificationArguments = new HashMap<>();
                    Competition competition = application.getCompetition();

                    notificationArguments.put("applicationName", application.getName());
                    notificationArguments.put("competitionName", competition.getName());
                    notificationArguments.put("webBaseUrl", webBaseUrl);

                    Notification notification = new Notification(
                            from,
                            singletonList(to),
                            Notifications.APPLICATION_SUBMITTED,
                            notificationArguments
                    );
                    return notificationService.sendNotification(notification, EMAIL);
        });
    }

    enum Notifications {
        APPLICATION_SUBMITTED,
        APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
        APPLICATION_INELIGIBLE
    }
}
