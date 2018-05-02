package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.InterviewAssessorAllocateApplicationsResourceBuilder.newInterviewAssessorAllocateApplicationsResource;
import static org.junit.Assert.assertEquals;

public class InterviewAssessorAllocateApplicationsResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        String expectedName = "name 1";
        String expectedSkillArea = "skills";

        InterviewAssessorAllocateApplicationsResource expectedInterviewAssessor = newInterviewAssessorAllocateApplicationsResource()
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

        List<InterviewAssessorAllocateApplicationsResource> expectedInterviewAssessors = newInterviewAssessorAllocateApplicationsResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withSkillArears(expectedSkillAreas)
                .build(2);

        InterviewAssessorAllocateApplicationsResource first = expectedInterviewAssessors.get(0);
        assertEquals((long) expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedSkillAreas[0], first.getSkillAreas());

        InterviewAssessorAllocateApplicationsResource second = expectedInterviewAssessors.get(1);
        assertEquals((long) expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedSkillAreas[1], second.getSkillAreas());
    }
}