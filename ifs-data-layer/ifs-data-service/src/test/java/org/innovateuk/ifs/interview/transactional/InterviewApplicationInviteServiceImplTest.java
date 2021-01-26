package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationSentInviteResourceBuilder.newInterviewApplicationSentInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentMessageOutcomeBuilder.newInterviewAssignmentMessageOutcome;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class InterviewApplicationInviteServiceImplTest extends BaseServiceUnitTest<InterviewApplicationInviteService> {

    private static final long COMPETITION_ID = 1L;
    private static final Organisation LEAD_ORGANISATION = newOrganisation().withName("lead org").build();

    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private InterviewAssignmentRepository interviewAssignmentRepositoryMock;

    @Mock
    private InterviewAssignmentWorkflowHandler interviewAssignmentWorkflowHandlerMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Override
    protected InterviewApplicationInviteServiceImpl supplyServiceUnderTest() {
        return new InterviewApplicationInviteServiceImpl();
    }

    @Test
    public void getEmailTemplate() {
        when(notificationTemplateRendererMock.renderTemplate(eq(systemNotificationSourceMock), any(NotificationTarget.class), eq(PREVIEW_TEMPLATES_PATH + "invite_applicants_to_interview_panel_text.txt"),
                any(Map.class))).thenReturn(serviceSuccess("Content"));

        ServiceResult<ApplicantInterviewInviteResource> result = service.getEmailTemplate();

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getContent(),"Content");
    }

    @Test
    public void sendInvites() {
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");
        List<InterviewAssignment> interviewAssignments = newInterviewAssignment()
                .withParticipant(
                        newProcessRole()
                                .withRole(ProcessRoleType.INTERVIEW_LEAD_APPLICANT)
                                .withOrganisationId(LEAD_ORGANISATION.getId())
                                .withUser(newUser()
                                        .withFirstName("Someone").withLastName("SomeName").withEmailAddress("someone@example.com").build())
                                .build()
                )
                .withTarget(
                        newApplication()
                                .withCompetition(newCompetition().build())
                                .build()

                )
                .withState(InterviewAssignmentState.CREATED)
                .build(1);

        InterviewAssignmentMessageOutcome outcome = new InterviewAssignmentMessageOutcome();
        outcome.setAssessmentInterviewPanel(interviewAssignments.get(0));
        outcome.setMessage(sendResource.getContent());
        outcome.setSubject(sendResource.getSubject());

        when(interviewAssignmentRepositoryMock.findByTargetCompetitionIdAndActivityState(
                COMPETITION_ID, InterviewAssignmentState.CREATED)).thenReturn(interviewAssignments);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess(null));

        ServiceResult<Void> result = service.sendInvites(COMPETITION_ID, sendResource);

        assertTrue(result.isSuccess());
        verify(notificationServiceMock, only()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));
        verify(interviewAssignmentWorkflowHandlerMock).notifyInterviewPanel(interviewAssignments.get(0), outcome);
    }

    @Test
    public void getSentInvite() {
        long applicationId = 1L;
        String subject = "subject";
        String content = "content";
        ZonedDateTime assigned = ZonedDateTime.now();

        InterviewApplicationSentInviteResource expected = newInterviewApplicationSentInviteResource()
                .withContent(content)
                .withSubject(subject)
                .withAssigned(assigned)
                .build();

        InterviewAssignmentMessageOutcome message = newInterviewAssignmentMessageOutcome()
                .withSubject(subject)
                .withMessage(content)
                .withCreatedOn(assigned)
                .build();

        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .withMessage(message)
                .build();

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);

        ServiceResult<InterviewApplicationSentInviteResource> result = service.getSentInvite(applicationId);

        assertTrue(result.isSuccess());

        assertEquals(expected, result.getSuccess());
    }

    @Test
    public void resendInvite() {
        long applicationId = 1L;
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");
        InterviewAssignmentMessageOutcome message = newInterviewAssignmentMessageOutcome().build();
        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .withParticipant(
                        newProcessRole()
                                .withRole(ProcessRoleType.INTERVIEW_LEAD_APPLICANT)
                                .withOrganisationId(LEAD_ORGANISATION.getId())
                                .withUser(newUser()
                                        .withFirstName("Someone").withLastName("SomeName").withEmailAddress("someone@example.com").build())
                                .build()
                )
                .withTarget(
                        newApplication()
                                .withCompetition(newCompetition().build())
                                .build()

                )
                .withState(InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE)
                .withMessage(message)
                .build();

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess(null));

        ServiceResult<Void> result = service.resendInvite(applicationId, sendResource);

        assertTrue(result.isSuccess());
        verify(notificationServiceMock, only()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));
        verify(interviewAssignmentWorkflowHandlerMock).notifyInterviewPanel(interviewAssignment, message);
    }
}