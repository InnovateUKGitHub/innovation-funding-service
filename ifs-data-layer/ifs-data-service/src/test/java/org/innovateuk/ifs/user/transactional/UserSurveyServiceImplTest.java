package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.transactional.UserSurveyServiceImpl.UserSurveyNotificationType.DIVERSITY_SURVEY_APPLICANT;
import static org.innovateuk.ifs.user.transactional.UserSurveyServiceImpl.UserSurveyNotificationType.DIVERSITY_SURVEY_ASSESSOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class UserSurveyServiceImplTest extends BaseServiceUnitTest<UserSurveyService> {

    private static final String diversitySurveyUrl = "https://www.surveymonkey.co.uk/r/ifsaccount";

    @Override
    protected UserSurveyService supplyServiceUnderTest() {
        UserSurveyService service = new UserSurveyServiceImpl();
        ReflectionTestUtils.setField(service, "diversitySurveyUrl", diversitySurveyUrl);
        return service;
    }

    @Test
    public void sendApplicantDiversitySurvey() throws Exception {
        User user = newUser()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .withEmailAddress("tom@poly.io")
                .build();

        when(notificationServiceMock.sendNotification(expectedNotification(user, DIVERSITY_SURVEY_APPLICANT), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        service.sendApplicantDiversitySurvey(user).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(notificationServiceMock);
        inOrder.verify(notificationServiceMock).sendNotification(expectedNotification(user, DIVERSITY_SURVEY_APPLICANT), eq(NotificationMedium.EMAIL));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAssessorDiversitySurvey() throws Exception {
        User user = newUser()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .withEmailAddress("tom@poly.io")
                .build();

        when(notificationServiceMock.sendNotification(expectedNotification(user, DIVERSITY_SURVEY_ASSESSOR), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        service.sendAssessorDiversitySurvey(user).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(notificationServiceMock);
        inOrder.verify(notificationServiceMock).sendNotification(expectedNotification(user, DIVERSITY_SURVEY_ASSESSOR), eq(NotificationMedium.EMAIL));
        inOrder.verifyNoMoreInteractions();
    }

    private Notification expectedNotification(User user, UserSurveyServiceImpl.UserSurveyNotificationType notificationType) {
       return createLambdaMatcher(n -> {
            NotificationTarget notificationTarget = n.getTo().get(0);
            assertEquals(user.getFirstName() + " " + user.getLastName(), notificationTarget.getName());
            assertEquals(user.getEmail(), notificationTarget.getEmailAddress());
            assertEquals(1, n.getGlobalArguments().size());
            assertEquals(diversitySurveyUrl, n.getGlobalArguments().get(UserSurveyServiceImpl.DIVERSITY_SURVEY_URL_KEY));
            assertEquals(notificationType, n.getMessageKey());
        } );
    }
}