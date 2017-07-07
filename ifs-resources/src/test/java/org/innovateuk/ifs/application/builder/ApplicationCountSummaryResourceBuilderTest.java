package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.junit.Assert.assertEquals;

public class ApplicationCountSummaryResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedName = "Tom Baldwin";
        String expectedLeadOrganisation = "Polymorphism";
        long expectedAssessors = 3L;
        long expectedAccepted = 5L;
        long expectedSubmitted = 7L;

        ApplicationCountSummaryResource applicationCountSummaryResource = newApplicationCountSummaryResource()
                .withId(expectedId)
                .withName(expectedName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withAssessors(expectedAssessors)
                .withAccepted(expectedAccepted)
                .withSubmitted(expectedSubmitted)
                .build();

        assertEquals(expectedId, applicationCountSummaryResource.getId());
        assertEquals(expectedName, applicationCountSummaryResource.getName());
        assertEquals(expectedLeadOrganisation, applicationCountSummaryResource.getLeadOrganisation());
        assertEquals(expectedAssessors, applicationCountSummaryResource.getAssessors());
        assertEquals(expectedAccepted, applicationCountSummaryResource.getAccepted());
        assertEquals(expectedSubmitted, applicationCountSummaryResource.getSubmitted());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"Tom Baldwin", "Duncan Watson"};
        String[] expectedLeadOrganisations = {"Polymorphism", "Hive IT"};
        Long[] expectedAssessors = {3L, 5L};
        Long[] expectedAccepteds = {7L, 11L};
        Long[] expectedSubmitteds = {13L, 17L};

        List<ApplicationCountSummaryResource> applicationCountSummaryResources = newApplicationCountSummaryResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withLeadOrganisation(expectedLeadOrganisations)
                .withAssessors(expectedAssessors)
                .withAccepted(expectedAccepteds)
                .withSubmitted(expectedSubmitteds)
                .build(2);

        ApplicationCountSummaryResource first = applicationCountSummaryResources.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisation());
        assertEquals(expectedAssessors[0].longValue(), first.getAssessors());
        assertEquals(expectedAccepteds[0].longValue(), first.getAccepted());
        assertEquals(expectedSubmitteds[0].longValue(), first.getSubmitted());

        ApplicationCountSummaryResource second = applicationCountSummaryResources.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisation());
        assertEquals(expectedAssessors[1].longValue(), second.getAssessors());
        assertEquals(expectedAccepteds[1].longValue(), second.getAccepted());
        assertEquals(expectedSubmitteds[1].longValue(), second.getSubmitted());
    }
}