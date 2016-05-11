package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationStatusBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.resource.FundingDecision.FUNDED;
import static com.worth.ifs.application.resource.FundingDecision.UNFUNDED;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_FUNDED;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_NOT_FUNDED;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_NOT_ALL_APPLICATIONS_REPRESENTED;
import static com.worth.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_NO_ASSESSOR_FEEDBACK_DATE_SET;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationFundingServiceImplMockTest extends BaseServiceUnitTest<ApplicationFundingService> {

    private static final String webBaseUrl = "http://ifs-local-dev";

    private ApplicationStatus approvedStatus;
    private ApplicationStatus rejectedStatus;

    @Override
    protected ApplicationFundingService supplyServiceUnderTest() {
        ApplicationFundingServiceImpl service = new ApplicationFundingServiceImpl();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }

    @Before
    public void setup() {
    	approvedStatus = ApplicationStatusBuilder.newApplicationStatus().build();
    	rejectedStatus = ApplicationStatusBuilder.newApplicationStatus().build();

    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.APPROVED.getId())).thenReturn(approvedStatus);
    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.REJECTED.getId())).thenReturn(rejectedStatus);
    }

    @Test
    public void testFailIfCompetitionNotFound() {
        when(competitionRepositoryMock.findOne(123L)).thenReturn(null);

        Map<Long, FundingDecision> decision = asMap(1L, FUNDED);

        ServiceResult<Void> result = service.makeFundingDecision(123L, decision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Competition.class, 123L)));
    }

    @Test
    public void testFailIfCompetitionHasNoAssessorFeedbackDate() {
        when(competitionRepositoryMock.findOne(123L)).thenReturn(newCompetition().build());

        Map<Long, FundingDecision> decision = asMap(1L, FUNDED);

        ServiceResult<Void> result = service.makeFundingDecision(123L, decision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FUNDING_PANEL_DECISION_NO_ASSESSOR_FEEDBACK_DATE_SET));
    }

    @Test
    public void testFailIfNotAllApplicationsRepresentedInDecision() {

        Competition competition = newCompetition().withAssessorFeedbackDate("01/02/2017 17:30:00").build();
        when(competitionRepositoryMock.findOne(123L)).thenReturn(competition);

        Application application1 = newApplication().withId(1L).withCompetition(competition).build();
    	Application application2 = newApplication().withId(2L).withCompetition(competition).build();
    	when(applicationRepositoryMock.findByCompetitionId(123L)).thenReturn(asList(application1, application2));
    	
    	Map<Long, FundingDecision> decision = asMap(1L, FUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FUNDING_PANEL_DECISION_NOT_ALL_APPLICATIONS_REPRESENTED));
    }
    
    @Test
    public void testSuccessAllApplicationsRepresented() {

        Competition competition = newCompetition().withAssessorFeedbackDate("01/02/2017 17:30:00").build();

        Application application1 = newApplication().withId(1L).withCompetition(competition).build();
    	Application application2 = newApplication().withId(2L).withCompetition(competition).build();
    	when(competitionRepositoryMock.findOne(123L)).thenReturn(competition);
    	
    	Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, UNFUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isSuccess());
    	verify(applicationRepositoryMock).save(application1);
    	verify(applicationRepositoryMock).save(application2);
    	assertEquals(approvedStatus, application1.getApplicationStatus());
    	assertEquals(rejectedStatus, application2.getApplicationStatus());
    }

	@Test
	public void testNotifyLeadApplicantsOfFundingDecisions() {

        Competition competition = newCompetition().withId(111L).withAssessorFeedbackDate(LocalDateTime.of(2017, 5, 3, 0, 0)).build();

        Application fundedApplication1 = newApplication().build();
		Application unfundedApplication2 = newApplication().build();
		Application fundedApplication3 = newApplication().build();

		User fundedApplication1LeadApplicant = newUser().build();
		User unfundedApplication2LeadApplicant = newUser().build();
		User fundedApplication3LeadApplicant = newUser().build();

		Role leadApplicantRole = newRole().with(id(456L)).build();

		List<ProcessRole> leadApplicantProcessRoles = newProcessRole().
				withUser(fundedApplication1LeadApplicant, unfundedApplication2LeadApplicant, fundedApplication3LeadApplicant).
				withApplication(fundedApplication1, unfundedApplication2, fundedApplication3).
				withRole(leadApplicantRole, leadApplicantRole, leadApplicantRole).
				build(3);

		Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, UNFUNDED, 3L, FUNDED);

        UserNotificationTarget fundedApplication1LeadApplicantTarget = new UserNotificationTarget(fundedApplication1LeadApplicant);
        UserNotificationTarget fundedApplication3LeadApplicantTarget = new UserNotificationTarget(fundedApplication3LeadApplicant);

        Map<String, Object> expectedGlobalNotificationArguments = asMap(
                "competitionName", competition.getName(),
                "dashboardUrl", webBaseUrl,
                "feedbackDate", competition.getAssessorFeedbackDate());

        List<NotificationTarget> expectedFundedLeadApplicants = asList(fundedApplication1LeadApplicantTarget, fundedApplication3LeadApplicantTarget);

        Map<NotificationTarget, Map<String, Object>> expectedFundedNotificationTargetSpecificArguments = asMap(
                fundedApplication1LeadApplicantTarget, asMap("applicationName", fundedApplication1.getName()),
                fundedApplication3LeadApplicantTarget, asMap("applicationName", fundedApplication3.getName()));

		Notification expectedFundedNotification = new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, APPLICATION_FUNDED,
                expectedGlobalNotificationArguments, expectedFundedNotificationTargetSpecificArguments);

        UserNotificationTarget unfundedApplication2LeadApplicantTarget = new UserNotificationTarget(unfundedApplication2LeadApplicant);
        List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(unfundedApplication2LeadApplicantTarget);

        Map<NotificationTarget, Map<String, Object>> expectedUnfundedNotificationTargetSpecificArguments = asMap(
                unfundedApplication2LeadApplicantTarget, asMap("applicationName", unfundedApplication2.getName()));

        Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, APPLICATION_NOT_FUNDED,
                expectedGlobalNotificationArguments, expectedUnfundedNotificationTargetSpecificArguments);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        when(roleRepositoryMock.findByName(LEADAPPLICANT.getName())).thenReturn(singletonList(leadApplicantRole));

		leadApplicantProcessRoles.forEach(processRole ->
				when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
		);

        asList(fundedApplication1, unfundedApplication2, fundedApplication3).forEach(application ->
            when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application)
        );

		when(notificationServiceMock.sendNotification(createFullNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedFundedNotification));
		when(notificationServiceMock.sendNotification(createFullNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedUnfundedNotification));

		ServiceResult<Void> result = service.notifyLeadApplicantsOfFundingDecisions(competition.getId(), decision);
		assertTrue(result.isSuccess());

		verify(notificationServiceMock).sendNotification(createFullNotificationExpectations(expectedFundedNotification), eq(EMAIL));
		verify(notificationServiceMock).sendNotification(createFullNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
		verifyNoMoreInteractions(notificationServiceMock);
	}

    @Test
    public void testNotifyLeadApplicantsOfFundingDecisionsAndJustLeadApplicants() {

        Competition competition = newCompetition().withId(111L).build();

        Application fundedApplication1 = newApplication().build();
        Application unfundedApplication2 = newApplication().build();

        // add some collaborators into the mix - they should not receive Notifications
        User fundedApplication1LeadApplicant = newUser().build();
        User fundedApplication1Collaborator = newUser().build();
        User unfundedApplication2LeadApplicant = newUser().build();
        User unfundedApplication2Collaborator = newUser().build();

        Role leadApplicantRole = newRole().with(id(456L)).build();
        Role collaboratorRole = newRole().with(id(789L)).build();

        List<ProcessRole> allProcessRoles = newProcessRole().
                withUser(fundedApplication1LeadApplicant, fundedApplication1Collaborator, unfundedApplication2LeadApplicant, unfundedApplication2Collaborator).
                withApplication(fundedApplication1, fundedApplication1, unfundedApplication2, unfundedApplication2).
                withRole(leadApplicantRole, collaboratorRole, leadApplicantRole, collaboratorRole).
                build(3);

        Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, UNFUNDED);

        List<NotificationTarget> expectedFundedLeadApplicants = asList(new UserNotificationTarget(fundedApplication1LeadApplicant));
        Notification expectedFundedNotification =
                new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, APPLICATION_FUNDED, emptyMap());

        List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(new UserNotificationTarget(unfundedApplication2LeadApplicant));
        Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, APPLICATION_NOT_FUNDED, emptyMap());

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        asList(fundedApplication1, unfundedApplication2).forEach(application ->
                when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application)
        );

        when(roleRepositoryMock.findByName(LEADAPPLICANT.getName())).thenReturn(singletonList(leadApplicantRole));

        allProcessRoles.forEach(processRole ->
                when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
        );

        when(notificationServiceMock.sendNotification(createSimpleNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedFundedNotification));
        when(notificationServiceMock.sendNotification(createSimpleNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedUnfundedNotification));

        ServiceResult<Void> result = service.notifyLeadApplicantsOfFundingDecisions(competition.getId(), decision);
        assertTrue(result.isSuccess());

        verify(notificationServiceMock).sendNotification(createSimpleNotificationExpectations(expectedFundedNotification), eq(EMAIL));
        verify(notificationServiceMock).sendNotification(createSimpleNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationServiceMock);
    }

    private Notification createFullNotificationExpectations(Notification expectedNotification) {

        return createLambdaMatcher(notification -> {
            assertEquals(expectedNotification.getFrom(), notification.getFrom());

            List<String> expectedToEmailAddresses = simpleMap(expectedNotification.getTo(), NotificationTarget::getEmailAddress);
            List<String> actualToEmailAddresses = simpleMap(notification.getTo(), NotificationTarget::getEmailAddress);
            assertEquals(expectedToEmailAddresses, actualToEmailAddresses);

            assertEquals(expectedNotification.getMessageKey(), notification.getMessageKey());
            assertEquals(expectedNotification.getGlobalArguments(), notification.getGlobalArguments());

            Map<NotificationTarget, Map<String, Object>> expectedTargetSpecifics = expectedNotification.getPerNotificationTargetArguments();
            Map<NotificationTarget, Map<String, Object>> actualTargetSpecifics = notification.getPerNotificationTargetArguments();

            assertEquals(expectedTargetSpecifics.size(), actualTargetSpecifics.size());

            expectedTargetSpecifics.forEach((target, expectedArguments) -> {
                Map<String, Object> actualArguments = actualTargetSpecifics.get(target);
                assertEquals(expectedArguments, actualArguments);
            });

            assertEquals(expectedTargetSpecifics, actualTargetSpecifics);
            return true;
        });
    }

	private Notification createSimpleNotificationExpectations(Notification expectedNotification) {

		return createLambdaMatcher(notification -> {
			assertEquals(expectedNotification.getFrom(), notification.getFrom());

			List<String> expectedToEmailAddresses = simpleMap(expectedNotification.getTo(), NotificationTarget::getEmailAddress);
			List<String> actualToEmailAddresses = simpleMap(notification.getTo(), NotificationTarget::getEmailAddress);
			assertEquals(expectedToEmailAddresses, actualToEmailAddresses);

			assertEquals(expectedNotification.getMessageKey(), notification.getMessageKey());
			return true;
		});
	}
}
