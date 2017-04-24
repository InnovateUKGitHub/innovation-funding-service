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
import static org.innovateuk.ifs.user.transactional.UserSurveyServiceImpl.Notifications.DIVERSITY_SURVEY;

@Service
public class UserSurveyServiceImpl implements UserSurveyService {

    static final String DIVERSITY_SURVEY_URL_KEY = "diversitySurveyUrl";

    enum Notifications {
        DIVERSITY_SURVEY;
    }

    @Value("${ifs.data.survey.diversity.url}")
    private String diversitySurveyUrl;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    public ServiceResult<Void> sendDiversitySurvey(User user) {
        return notificationService.sendNotification(surveyNotification(user), EMAIL);
    }

    private Notification surveyNotification(User user) {
        final Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put(DIVERSITY_SURVEY_URL_KEY, diversitySurveyUrl);
        return new Notification(systemNotificationSource, new UserNotificationTarget(user), DIVERSITY_SURVEY, notificationArguments);
    }
}