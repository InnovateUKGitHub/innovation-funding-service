package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerReviewNotificationServiceImpl.Notifications.MONITORING_OFFICER_NEW_DOCUMENT_REVIEW_NOTIFICATION;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class MonitoringOfficerReviewNotificationServiceImpl extends RootTransactionalService implements MonitoringOfficerReviewNotificationService {

    @Autowired
    private ProjectRepository projectRepository;
    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SystemNotificationSource systemNotificationSource;

    enum Notifications {
        MONITORING_OFFICER_NEW_DOCUMENT_REVIEW_NOTIFICATION
    }

    public ServiceResult<Void> sendDocumentReviewNotification(User monitoringOfficer, long projectId) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class))
                .andOnSuccess(project -> sendEmailToNotifyDocumentReview(monitoringOfficer, project)
        );
    }

    private ServiceResult<Void> sendEmailToNotifyDocumentReview(User user, Project project) {
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("monitoringOfficer", user);
        globalArgs.put("applicationName", project.getName());
        globalArgs.put("applicationId", project.getApplication().getId());
        globalArgs.put("dashboardUrl", webBaseUrl);

        return sendNotification(globalArgs,
                MONITORING_OFFICER_NEW_DOCUMENT_REVIEW_NOTIFICATION,
                new UserNotificationTarget(user.getName(), user.getEmail()));
    }

    private ServiceResult<Void> sendNotification(Map<String, Object> args, MonitoringOfficerReviewNotificationServiceImpl.Notifications notificationType, NotificationTarget target) {
        Notification notification = new Notification(systemNotificationSource,
                target,
                notificationType,
                args);

        ServiceResult<Void> sendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);
        return sendResult.handleSuccessOrFailure(
                failure -> serviceFailure(sendResult.getErrors()),
                success -> serviceSuccess()
        );

    }
}
