package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterviewAllocationServiceImplTest extends BaseServiceUnitTest<InterviewAllocationServiceImpl> {

    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;
    @Mock
    private InterviewRepository interviewRepository;
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
    private NotificationSender notificationSenderMock;

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

        when(interviewRepository.findApplicationsAssignedToAssessor(
                competitionId,
                userId,
                pageable
        )).thenReturn(pageResult);

        when(interviewRepository.countAllocatedApplications(competitionId, userId)).thenReturn(allocatedApplications);
        when(interviewRepository.countUnallocatedApplications(competitionId, userId)).thenReturn(unallocatedApplications);

        ServiceResult<InterviewApplicationPageResource> result =
                service.getAllocatedApplications(competitionId, userId, pageable);

        verify(interviewRepository)
                .findApplicationsAssignedToAssessor(competitionId, userId, pageable);
        verify(interviewRepository).countAllocatedApplications(competitionId, userId);
        verify(interviewRepository).countUnallocatedApplications(competitionId, userId);

        assertTrue(result.isSuccess());

        InterviewApplicationPageResource pageResource = result.getSuccess();

        assertEquals(pageResource.getContent(), expectedParticipants);
        assertEquals(pageResource.getUnallocatedApplications(), unallocatedApplications);
        assertEquals(pageResource.getAllocatedApplications(), allocatedApplications);
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

        when(interviewRepository.findApplicationsNotAssignedToAssessor(
                competitionId,
                userId,
                pageable
        )).thenReturn(pageResult);

        when(interviewRepository.countAllocatedApplications(competitionId, userId)).thenReturn(allocatedApplications);
        when(interviewRepository.countUnallocatedApplications(competitionId, userId)).thenReturn(unallocatedApplications);

        ServiceResult<InterviewApplicationPageResource> result =
                service.getUnallocatedApplications(competitionId, userId, pageable);

        verify(interviewRepository)
                .findApplicationsNotAssignedToAssessor(competitionId, userId, pageable);
        verify(interviewRepository).countAllocatedApplications(competitionId, userId);
        verify(interviewRepository).countUnallocatedApplications(competitionId, userId);

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

        when(interviewRepository.findApplicationIdsNotAssignedToAssessor(
                competitionId,
                userId
        )).thenReturn(ids);

        ServiceResult<List<Long>> result =
                service.getUnallocatedApplicationIds(competitionId, userId);

        verify(interviewRepository)
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
        String templatePath = "allocate_interview_applications_to_assessor_text.txt";
        Map<String, Object> notificationArguments = emptyMap();

        AssessorInvitesToSendResource expectedInvitesToSendResource =
                new AssessorInvitesToSendResource(singletonList(user.getName()), competition.getId(), competition.getName(), content);

        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, notificationArguments)).thenReturn(serviceSuccess(content));

        AssessorInvitesToSendResource actual = service.getInviteToSend(competition.getId(), user.getId()).getSuccess();

        assertEquals(expectedInvitesToSendResource, actual);
    }

    @Test
    public void notifyAllocation() {

    }
}