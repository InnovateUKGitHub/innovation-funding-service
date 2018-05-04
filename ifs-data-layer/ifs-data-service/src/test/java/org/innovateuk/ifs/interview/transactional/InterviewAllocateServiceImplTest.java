package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewResourceBuilder.newInterviewAssessorAllocateApplicationsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterviewAllocateServiceImplTest extends BaseServiceUnitTest<InterviewAllocateServiceImpl> {

    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;

    @Override
    protected InterviewAllocateServiceImpl supplyServiceUnderTest() {
        return new InterviewAllocateServiceImpl();
    }

    @Test
    public void getAllocateApplicationsOverview() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);

        List<InterviewAllocateOverviewResource> expectedParticipants = newInterviewAssessorAllocateApplicationsResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        Page<InterviewAllocateOverviewResource> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(interviewParticipantRepositoryMock.getAllocateApplicationsOverview(
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

        ServiceResult<InterviewAllocateOverviewPageResource> result =
                service.getAllocateApplicationsOverview(competitionId, pageable);

        verify(interviewParticipantRepositoryMock)
                .getAllocateApplicationsOverview(competitionId, pageable);

        assertTrue(result.isSuccess());

        InterviewAllocateOverviewPageResource pageResource = result.getSuccess();

        assertEquals(0, pageResource.getNumber());
        assertEquals(5, pageResource.getSize());
        assertEquals(2, pageResource.getTotalPages());
        assertEquals(10, pageResource.getTotalElements());

        List<InterviewAllocateOverviewResource> content = pageResource.getContent();
        assertEquals("Name 1", content.get(0).getName());
        assertEquals("Name 2", content.get(1).getName());
        assertEquals("Name 3", content.get(2).getName());
        assertEquals("Name 4", content.get(3).getName());
        assertEquals("Name 5", content.get(4).getName());
    }
}
