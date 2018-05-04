package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewResourceBuilder.newInterviewAssessorAllocateApplicationsResource;
import static org.junit.Assert.assertEquals;

public class InterviewAllocateOverviewResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        String expectedName = "name 1";
        String expectedSkillArea = "skills";

        InterviewAllocateOverviewResource expectedInterviewAssessor = newInterviewAssessorAllocateApplicationsResource()
                .withId(expectedId)
                .withName(expectedName)
                .withSkillArears(expectedSkillArea)
                .build();

        assertEquals(expectedId, expectedInterviewAssessor.getId());
        assertEquals(expectedName, expectedInterviewAssessor.getName());
        assertEquals(expectedSkillArea, expectedInterviewAssessor.getSkillAreas());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"name 1", "name 2"};
        String[] expectedSkillAreas = {"skill 1", "skill 2"};

        List<InterviewAllocateOverviewResource> expectedInterviewAssessors = newInterviewAssessorAllocateApplicationsResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withSkillArears(expectedSkillAreas)
                .build(2);

        InterviewAllocateOverviewResource first = expectedInterviewAssessors.get(0);
        assertEquals((long) expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedSkillAreas[0], first.getSkillAreas());

        InterviewAllocateOverviewResource second = expectedInterviewAssessors.get(1);
        assertEquals((long) expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedSkillAreas[1], second.getSkillAreas());
    }
}