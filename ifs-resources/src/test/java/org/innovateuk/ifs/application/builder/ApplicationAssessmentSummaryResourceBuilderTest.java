package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentSummaryResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedName = "application";
        Long expectedCompetitionId = 2L;
        String expectedCompetitionName = "competition";
        String expectedLeadOrganisation = "leadOrganisation";
        List<String> expectedPartnerOrganisations = asList("partnerOrganisation1", "partnerOrganisation2");

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(expectedId)
                .withName(expectedName)
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withPartnerOrganisations(expectedPartnerOrganisations)
                .build();

        assertEquals(expectedId, applicationAssessmentSummaryResource.getId());
        assertEquals(expectedName, applicationAssessmentSummaryResource.getName());
        assertEquals(expectedCompetitionId, applicationAssessmentSummaryResource.getCompetitionId());
        assertEquals(expectedCompetitionName, applicationAssessmentSummaryResource.getCompetitionName());
        assertEquals(expectedLeadOrganisation, applicationAssessmentSummaryResource.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations, applicationAssessmentSummaryResource.getPartnerOrganisations());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"name1", "name2"};
        Long[] expectedCompetitionIds = {3L, 4L};
        String[] expectedCompetitionNames = {"competition1", "competition2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation"};
        List<String> expectedPartnerOrganisations1 = asList("partnerOrganisation1", "partnerOrganisation2");
        List<String> expectedPartnerOrganisations2 = asList("partnerOrganisation3", "partnerOrganisation4");

        List<ApplicationAssessmentSummaryResource> applicationAssessmentSummaryResources = newApplicationAssessmentSummaryResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withLeadOrganisation(expectedLeadOrganisations)
                .withPartnerOrganisations(expectedPartnerOrganisations1, expectedPartnerOrganisations2)
                .build(2);

        ApplicationAssessmentSummaryResource first = applicationAssessmentSummaryResources.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedCompetitionIds[0], first.getCompetitionId());
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations1, first.getPartnerOrganisations());

        ApplicationAssessmentSummaryResource second = applicationAssessmentSummaryResources.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations2, second.getPartnerOrganisations());
    }

}