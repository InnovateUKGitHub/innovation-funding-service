package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectNotificationServiceImpl implements ProjectNotificationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        PROJECT_SETUP_KTP
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Void> sendProjectSetupNotification(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    NotificationSource from = systemNotificationSource;
                    List<NotificationTarget> notificationTargets = application.getApplicantProcessRoles().stream()
                            .filter(processRole -> processRole.isLeadApplicant() || processRole.isKta())
                            .map(ProcessRole::getUser)
                            .filter(User::isActive)
                            .map(applicant -> new UserNotificationTarget(applicant.getName(), applicant.getEmail()))
                            .collect(Collectors.toList());

                    Competition competition = application.getCompetition();
                    Notification notification = projectSetupNotification(from, notificationTargets, competition);

                    return notificationService.sendNotificationWithFlush(notification, EMAIL);
                });
    }

    private Notification projectSetupNotification(NotificationSource from, List<NotificationTarget> notificationTargets,
                                                  Competition competition) {
        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("competitionNumber", competition.getId());
        notificationArguments.put("competitionName", competition.getName());
        notificationArguments.put("dashboardUrl", webBaseUrl);

        return new Notification(
                from,
                notificationTargets,
                Notifications.PROJECT_SETUP_KTP,
                notificationArguments
        );
    }
}
