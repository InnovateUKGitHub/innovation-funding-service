package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.transactional.UserSurveyServiceImpl.UserSurveyNotificationType.DIVERSITY_SURVEY_APPLICANT;
import static org.innovateuk.ifs.user.transactional.UserSurveyServiceImpl.UserSurveyNotificationType.DIVERSITY_SURVEY_ASSESSOR;

@Service
public class UserSurveyServiceImpl implements UserSurveyService {

    static final String DIVERSITY_SURVEY_URL_KEY = "diversitySurveyUrl";

    enum UserSurveyNotificationType {
        DIVERSITY_SURVEY_APPLICANT,
        DIVERSITY_SURVEY_ASSESSOR
    }

    @Value("${ifs.data.survey.diversity.url}")
    private String diversitySurveyUrl;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    public ServiceResult<Void> sendApplicantDiversitySurvey(User user) {
        return notificationService.sendNotification(surveyNotification(user, DIVERSITY_SURVEY_APPLICANT), EMAIL);
    }

    @Override
    public ServiceResult<Void> sendAssessorDiversitySurvey(User user) {
        return notificationService.sendNotification(surveyNotification(user, DIVERSITY_SURVEY_ASSESSOR), EMAIL);
    }

    private Notification surveyNotification(User user, UserSurveyNotificationType notificationType) {
        final Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put(DIVERSITY_SURVEY_URL_KEY, diversitySurveyUrl);
        return new Notification(systemNotificationSource, new UserNotificationTarget(user), notificationType, notificationArguments);
    }
}