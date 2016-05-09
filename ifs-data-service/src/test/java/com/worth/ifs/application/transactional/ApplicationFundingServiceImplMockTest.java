package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationStatusBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.resource.FundingDecision.FUNDED;
import static com.worth.ifs.application.resource.FundingDecision.NOT_FUNDED;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationFundingServiceImplMockTest extends BaseServiceUnitTest<ApplicationFundingService> {

    @Override
    protected ApplicationFundingService supplyServiceUnderTest() {
        return new ApplicationFundingServiceImpl();
    }

    private ApplicationStatus approvedStatus;
    private ApplicationStatus rejectedStatus;

    @Before
    public void setup() {
    	approvedStatus = ApplicationStatusBuilder.newApplicationStatus().build();
    	rejectedStatus = ApplicationStatusBuilder.newApplicationStatus().build();

    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.APPROVED.getId())).thenReturn(approvedStatus);
    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.REJECTED.getId())).thenReturn(rejectedStatus);
    }

    @Test
    public void testFailIfNotAllApplicationsRepresentedInDecision() {
    	Application application1 = newApplication().withId(1L).build();
    	Application application2 = newApplication().withId(2L).build();
    	when(applicationRepositoryMock.findByCompetitionId(123L)).thenReturn(asList(application1, application2));
    	
    	Map<Long, FundingDecision> decision = asMap(1L, FUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isFailure());
    	assertEquals("not all applications represented in funding decision", result.getFailure().getErrors().get(0).getErrorMessage());
    	verify(applicationRepositoryMock).findByCompetitionId(123L);
    	verifyNoMoreInteractions(applicationRepositoryMock);
    }
    
    @Test
    public void testSuccessAllApplicationsRepresented() {
    	Application application1 = newApplication().withId(1L).build();
    	Application application2 = newApplication().withId(2L).build();
    	when(applicationRepositoryMock.findByCompetitionId(123L)).thenReturn(asList(application1, application2));
    	
    	Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, NOT_FUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isSuccess());
    	verify(applicationRepositoryMock).findByCompetitionId(123L);
    	verify(applicationRepositoryMock).save(application1);
    	verify(applicationRepositoryMock).save(application2);
    	assertEquals(approvedStatus, application1.getApplicationStatus());
    	assertEquals(rejectedStatus, application2.getApplicationStatus());
    }

	@Test
	public void testNotifyLeadApplicantsOfFundingDecisions() {

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

		Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, NOT_FUNDED, 3L, FUNDED);

		List<NotificationTarget> expectedFundedLeadApplicants = asList(new UserNotificationTarget(fundedApplication1LeadApplicant), new UserNotificationTarget(fundedApplication3LeadApplicant));
		Notification expectedFundedNotification = new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, ApplicationFundingServiceImpl.Notifications.FUNDED_APPLICATION, emptyMap());

		List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(new UserNotificationTarget(unfundedApplication2LeadApplicant));
		Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, ApplicationFundingServiceImpl.Notifications.UNFUNDED_APPLICATION, emptyMap());

		when(roleRepositoryMock.findByName(LEADAPPLICANT.getName())).thenReturn(singletonList(leadApplicantRole));

		leadApplicantProcessRoles.forEach(processRole ->
				when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
		);

		when(notificationServiceMock.sendNotification(createNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedFundedNotification));
		when(notificationServiceMock.sendNotification(createNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedUnfundedNotification));

		ServiceResult<Void> result = service.notifyLeadApplicantsOfFundingDecisions(123L, decision);
		assertTrue(result.isSuccess());

		verify(notificationServiceMock).sendNotification(createNotificationExpectations(expectedFundedNotification), eq(EMAIL));
		verify(notificationServiceMock).sendNotification(createNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
		verifyNoMoreInteractions(notificationServiceMock);
	}

    @Test
    public void testNotifyLeadApplicantsOfFundingDecisionsAndJustLeadApplicants() {

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

        Map<Long, FundingDecision> decision = asMap(1L, FUNDED, 2L, NOT_FUNDED);

        List<NotificationTarget> expectedFundedLeadApplicants = asList(new UserNotificationTarget(fundedApplication1LeadApplicant));
        Notification expectedFundedNotification = new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, ApplicationFundingServiceImpl.Notifications.FUNDED_APPLICATION, emptyMap());

        List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(new UserNotificationTarget(unfundedApplication2LeadApplicant));
        Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, ApplicationFundingServiceImpl.Notifications.UNFUNDED_APPLICATION, emptyMap());

        when(roleRepositoryMock.findByName(LEADAPPLICANT.getName())).thenReturn(singletonList(leadApplicantRole));

        allProcessRoles.forEach(processRole ->
                when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
        );

        when(notificationServiceMock.sendNotification(createNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedFundedNotification));
        when(notificationServiceMock.sendNotification(createNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess(expectedUnfundedNotification));

        ServiceResult<Void> result = service.notifyLeadApplicantsOfFundingDecisions(123L, decision);
        assertTrue(result.isSuccess());

        verify(notificationServiceMock).sendNotification(createNotificationExpectations(expectedFundedNotification), eq(EMAIL));
        verify(notificationServiceMock).sendNotification(createNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationServiceMock);
    }

	private Notification createNotificationExpectations(Notification expectedNotification) {

		return createLambdaMatcher(notification -> {
			assertEquals(expectedNotification.getFrom(), notification.getFrom());

			List<String> expectedToEmailAddresses = simpleMap(expectedNotification.getTo(), NotificationTarget::getEmailAddress);
			List<String> actualToEmailAddresses = simpleMap(notification.getTo(), NotificationTarget::getEmailAddress);
			assertEquals(expectedToEmailAddresses, actualToEmailAddresses);

			assertEquals(expectedNotification.getMessageKey(), notification.getMessageKey());
			assertEquals(expectedNotification.getArguments(), notification.getArguments());
			return true;
		});
	}
}
