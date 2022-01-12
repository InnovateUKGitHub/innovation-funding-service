package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewNotifyAllocationResource;
import org.junit.Test;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;
import static org.junit.Assert.assertEquals;

public class InterviewNotifyAllocationResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedCompetitionId = 1L;
        long expectedAssessorId = 2L;
        String expectedSubject = "subject";
        String expectedContent = "content";
        List<Long> expectedApplicationIds = asList(3L, 5L);

        InterviewNotifyAllocationResource interviewNotifyAllocationResource = newInterviewNotifyAllocationResource()
                .withCompetitionId(expectedCompetitionId)
                .withAssessorId(expectedAssessorId)
                .withSubject(expectedSubject)
                .withContent(expectedContent)
                .withApplicationIds(expectedApplicationIds)
                .build();

        assertEquals(expectedCompetitionId, interviewNotifyAllocationResource.getCompetitionId());
        assertEquals(expectedAssessorId, interviewNotifyAllocationResource.getAssessorId());
        assertEquals(expectedSubject, interviewNotifyAllocationResource.getSubject());
        assertEquals(expectedContent, interviewNotifyAllocationResource.getContent());
        assertEquals(expectedApplicationIds, interviewNotifyAllocationResource.getApplicationIds());
    }

    @Test
    public void buildMany() {
        Long[] expectedCompetitionIds = {1L, 2L};
        Long[] expectedAssessorIds = {3L, 5L};
        String[] expectedSubjects = {"subject1", "subject2"};
        String[] expectedContents = {"content1", "content2"};
        List<Long>[] expectedApplicationIds = new List[]{asList(7L, 1L), asList(13L, 17L)};


        List<InterviewNotifyAllocationResource> interviewNotifyAllocationResources = newInterviewNotifyAllocationResource()
                .withCompetitionId(expectedCompetitionIds)
                .withAssessorId(expectedAssessorIds)
                .withSubject(expectedSubjects)
                .withContent(expectedContents)
                .withApplicationIds(expectedApplicationIds)
                .build(2);

        for (int i = 0; i < interviewNotifyAllocationResources.size(); i++) {
            assertEquals((long) expectedCompetitionIds[i], interviewNotifyAllocationResources.get(i).getCompetitionId());
            assertEquals((long) expectedAssessorIds[i], interviewNotifyAllocationResources.get(i).getAssessorId());
            assertEquals(expectedSubjects[i], interviewNotifyAllocationResources.get(i).getSubject());
            assertEquals(expectedContents[i], interviewNotifyAllocationResources.get(i).getContent());
            assertEquals(expectedApplicationIds[i], interviewNotifyAllocationResources.get(i).getApplicationIds());
        }
    }
}