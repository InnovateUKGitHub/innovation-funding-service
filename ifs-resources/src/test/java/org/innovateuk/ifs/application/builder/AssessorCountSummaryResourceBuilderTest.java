package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.junit.Assert.assertEquals;

public class AssessorCountSummaryResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedName = "Tom Baldwin";
        long expectedTotalAssigned = 3L;
        long expectedAssigned = 5L;
        long expectedAccepted = 7L;
        long expectedSubmitted = 11L;

        AssessorCountSummaryResource applicationCountSummaryResource = newAssessorCountSummaryResource()
                .withId(expectedId)
                .withName(expectedName)
                .withTotalAssigned(expectedTotalAssigned)
                .withAssigned(expectedAssigned)
                .withAccepted(expectedAccepted)
                .withSubmitted(expectedSubmitted)
                .build();

        assertEquals(expectedId, applicationCountSummaryResource.getId());
        assertEquals(expectedName, applicationCountSummaryResource.getName());
        assertEquals(expectedTotalAssigned, applicationCountSummaryResource.getTotalAssigned());
        assertEquals(expectedAssigned, applicationCountSummaryResource.getAssigned());
        assertEquals(expectedAccepted, applicationCountSummaryResource.getAccepted());
        assertEquals(expectedSubmitted, applicationCountSummaryResource.getSubmitted());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"Tom Baldwin", "Duncan Watson"};
        Long[] expectedTotalAssigneds = {3L, 5L};
        Long[] expectedAssigneds = {7L, 11L};
        Long[] expectedAccepteds = {13L, 17L};
        Long[] expectedSubmitteds = {19L, 23L};

        List<AssessorCountSummaryResource> assessorCountSummaryResources = newAssessorCountSummaryResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withTotalAssigned(expectedTotalAssigneds)
                .withAssigned(expectedAssigneds)
                .withAccepted(expectedAccepteds)
                .withSubmitted(expectedSubmitteds)
                .build(2);

        AssessorCountSummaryResource first = assessorCountSummaryResources.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedTotalAssigneds[0].longValue(), first.getTotalAssigned());
        assertEquals(expectedAssigneds[0].longValue(), first.getAssigned());
        assertEquals(expectedAccepteds[0].longValue(), first.getAccepted());
        assertEquals(expectedSubmitteds[0].longValue(), first.getSubmitted());

        AssessorCountSummaryResource second = assessorCountSummaryResources.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedTotalAssigneds[1].longValue(), second.getTotalAssigned());
        assertEquals(expectedAssigneds[1].longValue(), second.getAssigned());
        assertEquals(expectedAccepteds[1].longValue(), second.getAccepted());
        assertEquals(expectedSubmitteds[1].longValue(), second.getSubmitted());
    }
}