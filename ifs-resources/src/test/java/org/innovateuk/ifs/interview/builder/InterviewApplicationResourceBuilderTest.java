package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.junit.Assert.assertEquals;

public class InterviewApplicationResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        String expectedName = "name 1";
        String expectedLeadOrg = "leadOrg";
        long expectedNumberOfAssessors = 2L;


        InterviewApplicationResource expectedApplication = newInterviewApplicationResource()
                .withId(expectedId)
                .withName(expectedName)
                .withLeadOrganisation(expectedLeadOrg)
                .withNumberOfAssessors(expectedNumberOfAssessors)
                .build();

        assertEquals(expectedId, expectedApplication.getId());
        assertEquals(expectedName, expectedApplication.getName());
        assertEquals(expectedLeadOrg, expectedApplication.getLeadOrganisation());
        assertEquals(expectedNumberOfAssessors, expectedApplication.getNumberOfAssessors());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"name 1", "name 2"};
        String[] expectedOrgNames = {"Org 1", "Org 2"};
        Long[] expectedNumberOfAssessors = {1L, 2L};

        List<InterviewApplicationResource> expectedInterviewAssessors = newInterviewApplicationResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withLeadOrganisation(expectedOrgNames)
                .withNumberOfAssessors(expectedNumberOfAssessors)
                .build(2);

        InterviewApplicationResource first = expectedInterviewAssessors.get(0);
        assertEquals((long) expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedOrgNames[0], first.getLeadOrganisation());
        assertEquals((long) expectedNumberOfAssessors[0], first.getNumberOfAssessors());

        InterviewApplicationResource second = expectedInterviewAssessors.get(1);
        assertEquals((long) expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedOrgNames[1], second.getLeadOrganisation());
        assertEquals((long) expectedNumberOfAssessors[1], second.getNumberOfAssessors());
    }
}