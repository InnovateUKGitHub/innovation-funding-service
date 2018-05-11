package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
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

    @Override
    protected InterviewAllocationServiceImpl supplyServiceUnderTest() {
        return new InterviewAllocationServiceImpl();
    }

    @Test
    public void getAllocateApplicationsOverview() throws Exception {
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
    public void getAllocatedApplications() throws Exception {
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
    public void getUnallocatedApplications() throws Exception {
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
    public void getUnallocatedApplicationIds() throws Exception {
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
}
