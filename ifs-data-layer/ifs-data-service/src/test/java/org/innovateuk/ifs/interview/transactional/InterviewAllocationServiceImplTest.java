package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.mapper.InterviewMapper;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewWorkflowHandler;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewBuilder.newInterview;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantBuilder.newInterviewParticipant;
import static org.innovateuk.ifs.interview.builder.InterviewResourceBuilder.newInterviewResource;
import static org.innovateuk.ifs.interview.transactional.InterviewAllocationServiceImpl.Notifications.NOTIFY_ASSESSOR_OF_INTERVIEW_ALLOCATIONS;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class InterviewAllocationServiceImplTest extends BaseServiceUnitTest<InterviewAllocationServiceImpl> {

    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;
    @Mock
    private InterviewRepository interviewRepositoryMock;
    @Mock
    private InterviewParticipantRepository interviewParticipantRepositoryMock;
    @Mock
    private CompetitionRepository competitionRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;
    @Mock
    private SystemNotificationSource systemNotificationSourceMock;
    @Mock
    private NotificationService notificationServiceMock;
    @Mock
    private ApplicationRepository applicationRepositoryMock;
    @Mock
    private InterviewWorkflowHandler interviewWorkflowHandlerMock;
    @Mock
    private InterviewMapper interviewMapper;

    @Override
    protected InterviewAllocationServiceImpl supplyServiceUnderTest() {
        return new InterviewAllocationServiceImpl();
    }

    @Test
    public void getAllocateApplicationsOverview() {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);

        List<InterviewAcceptedAssessorsResource> expectedParticipants = newInterviewAcceptedAssessorsResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        Page<InterviewAcceptedAssessorsResource> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(interviewParticipantRepositoryMock.getInterviewAcceptedAssessorsByCompetition(
                competitionId,
                pageable
        ))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapperMock.mapToResource(isA(InterviewParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );

        ServiceResult<InterviewAcceptedAssessorsPageResource> result =
                service.getInterviewAcceptedAssessors(competitionId, pageable);

        verify(interviewParticipantRepositoryMock)
                .getInterviewAcceptedAssessorsByCompetition(competitionId, pageable);

        assertTrue(result.isSuccess());

        InterviewAcceptedAssessorsPageResource pageResource = result.getSuccess();

        assertEquals(0, pageResource.getNumber());
        assertEquals(5, pageResource.getSize());
        assertEquals(2, pageResource.getTotalPages());
        assertEquals(10, pageResource.getTotalElements());

        List<InterviewAcceptedAssessorsResource> content = pageResource.getContent();
        assertEquals("Name 1", content.get(0).getName());
        assertEquals("Name 2", content.get(1).getName());
        assertEquals("Name 3", content.get(2).getName());
        assertEquals("Name 4", content.get(3).getName());
        assertEquals("Name 5", content.get(4).getName());
    }

    @Test
    public void getAllocatedApplications() {
        long competitionId = 1L;
        long userId = 2L;
        Pageable pageable = new PageRequest(0, 5);

        long allocatedApplications = 3L;
        long unallocatedApplications = 4L;

        List<InterviewApplicationResource> expectedParticipants = newInterviewApplicationResource()
                .build(1);

        Page<InterviewApplicationResource> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(interviewRepositoryMock.findApplicationsAssignedToAssessor(
                competitionId,
                userId,
                pageable
        )).thenReturn(pageResult);

        when(interviewRepositoryMock.countAllocatedApplications(competitionId, userId)).thenReturn(allocatedApplications);
        when(interviewRepositoryMock.countUnallocatedApplications(competitionId, userId)).thenReturn(unallocatedApplications);

        ServiceResult<InterviewApplicationPageResource> result =
                service.getAllocatedApplications(competitionId, userId, pageable);

        verify(interviewRepositoryMock)
                .findApplicationsAssignedToAssessor(competitionId, userId, pageable);
        verify(interviewRepositoryMock).countAllocatedApplications(competitionId, userId);
        verify(interviewRepositoryMock).countUnallocatedApplications(competitionId, userId);

        assertTrue(result.isSuccess());

        InterviewApplicationPageResource pageResource = result.getSuccess();

        assertEquals(pageResource.getContent(), expectedParticipants);
        assertEquals(pageResource.getUnallocatedApplications(), unallocatedApplications);
        assertEquals(pageResource.getAllocatedApplications(), allocatedApplications);
    }

    @Test
    public void getAllocatedApplicationsByAssessorId() {
        long competitionId = 1L;

        User user = newUser()
                .withId(1L)
                .build();

        Application application1 = newApplication()
                .withId(1L)
                .withName("Application 1")
                .build();

        Application application2 = newApplication()
                .withId(1L)
                .withName("Application 1")
                .build();

        ProcessRole processRole1 = newProcessRole()
                .withApplication(application1)
                .withUser(user)
                .build();

        ProcessRole processRole2 = newProcessRole()
                .withApplication(application2)
                .withUser(user)
                .build();

        List<Interview> expectedInterviews = newInterview()
                .withTarget(application1, application2)
                .withParticipant(processRole1, processRole2)
                .build(2);

        List<InterviewResource> expectedInterviewResources = newInterviewResource()
                .withApplication(application1.getId(), application2.getId())
                .withProcessRole(processRole1.getId(), processRole2.getId())
                .build(2);

        when(interviewRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(
                competitionId,
                user.getId()
        )).thenReturn(expectedInterviews);

        when(interviewMapper.mapToResource(expectedInterviews.get(0))).thenReturn(expectedInterviewResources.get(0));
        when(interviewMapper.mapToResource(expectedInterviews.get(1))).thenReturn(expectedInterviewResources.get(1));

        ServiceResult<List<InterviewResource>> result =
                service.getAllocatedApplicationsByAssessorId(competitionId, user.getId());

        verify(interviewRepositoryMock)
                .findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(competitionId, user.getId());

        assertTrue(result.isSuccess());

        List<InterviewResource> interviewResources = result.getSuccess();

        assertEquals(interviewResources.get(0).getApplication(), expectedInterviews.get(0).getTarget().getId());
        assertEquals(processRole1, expectedInterviews.get(0).getParticipant());

        assertEquals(interviewResources.get(1).getApplication(), expectedInterviews.get(1).getTarget().getId());
        assertEquals(processRole2, expectedInterviews.get(1).getParticipant());
        assertEquals(interviewResources.size(), expectedInterviews.size());
    }

    @Test
    public void getUnallocatedApplications() {
        long competitionId = 1L;
        long userId = 2L;
        Pageable pageable = new PageRequest(0, 5);

        long allocatedApplications = 3L;
        long unallocatedApplications = 4L;

        List<InterviewApplicationResource> expectedParticipants = newInterviewApplicationResource()
                .build(1);

        Page<InterviewApplicationResource> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(interviewRepositoryMock.findApplicationsNotAssignedToAssessor(
                competitionId,
                userId,
                pageable
        )).thenReturn(pageResult);

        when(interviewRepositoryMock.countAllocatedApplications(competitionId, userId)).thenReturn(allocatedApplications);
        when(interviewRepositoryMock.countUnallocatedApplications(competitionId, userId)).thenReturn(unallocatedApplications);

        ServiceResult<InterviewApplicationPageResource> result =
                service.getUnallocatedApplications(competitionId, userId, pageable);

        verify(interviewRepositoryMock)
                .findApplicationsNotAssignedToAssessor(competitionId, userId, pageable);
        verify(interviewRepositoryMock).countAllocatedApplications(competitionId, userId);
        verify(interviewRepositoryMock).countUnallocatedApplications(competitionId, userId);

        assertTrue(result.isSuccess());

        InterviewApplicationPageResource pageResource = result.getSuccess();

        assertEquals(pageResource.getContent(), expectedParticipants);
        assertEquals(pageResource.getUnallocatedApplications(), unallocatedApplications);
        assertEquals(pageResource.getAllocatedApplications(), allocatedApplications);
    }

    @Test
    public void getUnallocatedApplicationIds() {
        long competitionId = 1L;
        long userId = 2L;
        List<Long> ids = asList(4L, 5L);

        when(interviewRepositoryMock.findApplicationIdsNotAssignedToAssessor(
                competitionId,
                userId
        )).thenReturn(ids);

        ServiceResult<List<Long>> result =
                service.getUnallocatedApplicationIds(competitionId, userId);

        verify(interviewRepositoryMock)
                .findApplicationIdsNotAssignedToAssessor(competitionId, userId);

        assertTrue(result.isSuccess());

        assertEquals(result.getSuccess(), ids);
    }

    @Test
    public void getInviteToSend() {
        Competition competition = newCompetition().build();
        User user = newUser().build();
        String content = "content";
        NotificationTarget notificationTarget = new UserNotificationTarget("", "");
        String templatePath = PREVIEW_TEMPLATES_PATH + "allocate_interview_applications_to_assessor_text.txt";
        Map<String, Object> notificationArguments = asMap(
                "name", user.getName(),
                "competitionName", competition.getName()
        );

        AssessorInvitesToSendResource expectedInvitesToSendResource =
                new AssessorInvitesToSendResource(singletonList(user.getName()), competition.getId(), competition.getName(), content);

        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, notificationArguments)).thenReturn(serviceSuccess(content));

        AssessorInvitesToSendResource actual = service.getInviteToSend(competition.getId(), user.getId()).getSuccess();

        assertEquals(expectedInvitesToSendResource, actual);

        InOrder inOrder = inOrder(competitionRepositoryMock, userRepositoryMock, notificationTemplateRendererMock, systemNotificationSourceMock);
        inOrder.verify(competitionRepositoryMock).findById(competition.getId());
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath, notificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAllocation() {
        Competition competition = newCompetition().build();
        User user = newUser().withFirstName("tom").withLastName("baldwin").withEmailAddress("tom@poly.io").build();
        InterviewParticipant interviewParticipant = newInterviewParticipant().withUser(user)
                .withCompetition(newCompetition().build())
                .build();
        String subject = "subject";
        String content = "content";
        List<Application> applications = newApplication().build(1);

        InterviewNotifyAllocationResource interviewNotifyAllocationResource =
                new InterviewNotifyAllocationResource(competition.getId(), user.getId(), subject, content, simpleMap(applications, Application::getId));

        Interview interview = new Interview(applications.get(0), interviewParticipant);     

        Notification expectedNotification = new Notification(
                systemNotificationSourceMock,
                new UserNotificationTarget(user.getName(), user.getEmail()),
                NOTIFY_ASSESSOR_OF_INTERVIEW_ALLOCATIONS,
                asMap(
                        "subject", subject,
                        "name", user.getName(),
                        "competitionName", interviewParticipant.getProcess().getName(),
                        "customTextPlain", content,
                        "customTextHtml", content
                ));

        when(applicationRepositoryMock.findOne(applications.get(0).getId())).thenReturn(applications.get(0));
        when(interviewParticipantRepositoryMock
                .findByUserIdAndCompetitionIdAndRole(user.getId(), competition.getId(), CompetitionParticipantRole.INTERVIEW_ASSESSOR))
                .thenReturn(interviewParticipant);
        when(notificationServiceMock.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        service.notifyAllocation(interviewNotifyAllocationResource).getSuccess();

        InOrder inOrder = inOrder(notificationServiceMock, applicationRepositoryMock, interviewParticipantRepositoryMock, interviewWorkflowHandlerMock, interviewRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(applications.get(0).getId());
        inOrder.verify(interviewWorkflowHandlerMock).notifyInvitation(interview);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(expectedNotification, EMAIL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUnallocatedApplicationsById() {
        Long[] applicationIds = {1L, 2L};
        List<InterviewApplicationResource> expectedInterviewApplications = newInterviewApplicationResource().build(2);

        when(interviewRepositoryMock.findAllNotified(asList(applicationIds))).thenReturn(expectedInterviewApplications);

        List<InterviewApplicationResource> actualInterviewApplications = service.getUnallocatedApplicationsById(asList(applicationIds)).getSuccess();

        assertEquals(expectedInterviewApplications, actualInterviewApplications);

        verify(interviewRepositoryMock, only()).findAllNotified(asList(applicationIds));
    }
}