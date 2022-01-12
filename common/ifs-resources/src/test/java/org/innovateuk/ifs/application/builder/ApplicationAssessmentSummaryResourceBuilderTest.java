package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentSummaryResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        String expectedName = "application";
        String expectedInnovationArea = "innovation area";
        Long expectedCompetitionId = 2L;
        String expectedCompetitionName = "competition";
        String expectedLeadOrganisation = "leadOrganisation";
        CompetitionStatus expectedCompetitionStatus = OPEN;
        List<String> expectedPartnerOrganisations = asList("partnerOrganisation1", "partnerOrganisation2");

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(expectedId)
                .withName(expectedName)
                .withInnovationArea(expectedInnovationArea)
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withCompetitionStatus(expectedCompetitionStatus)
                .withPartnerOrganisations(expectedPartnerOrganisations)
                .build();

        assertEquals(expectedId, applicationAssessmentSummaryResource.getId());
        assertEquals(expectedName, applicationAssessmentSummaryResource.getName());
        assertEquals(expectedInnovationArea, applicationAssessmentSummaryResource.getInnovationArea());
        assertEquals(expectedCompetitionId, applicationAssessmentSummaryResource.getCompetitionId());
        assertEquals(expectedCompetitionName, applicationAssessmentSummaryResource.getCompetitionName());
        assertEquals(expectedLeadOrganisation, applicationAssessmentSummaryResource.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations, applicationAssessmentSummaryResource.getPartnerOrganisations());
        assertEquals(expectedCompetitionStatus, applicationAssessmentSummaryResource.getCompetitionStatus());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"name1", "name2"};
        String[] expectedInnovationAreas = {"innovation area1", "innovation area2"};
        Long[] expectedCompetitionIds = {3L, 4L};
        String[] expectedCompetitionNames = {"competition1", "competition2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation"};
        List<String> expectedPartnerOrganisations1 = asList("partnerOrganisation1", "partnerOrganisation2");
        List<String> expectedPartnerOrganisations2 = asList("partnerOrganisation3", "partnerOrganisation4");
        CompetitionStatus[] expectedCompetitionStatuses = {OPEN, CLOSED};

        List<ApplicationAssessmentSummaryResource> applicationAssessmentSummaryResources = newApplicationAssessmentSummaryResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withInnovationArea(expectedInnovationAreas)
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withLeadOrganisation(expectedLeadOrganisations)
                .withPartnerOrganisations(expectedPartnerOrganisations1, expectedPartnerOrganisations2)
                .withCompetitionStatus(expectedCompetitionStatuses)
                .build(2);

        ApplicationAssessmentSummaryResource first = applicationAssessmentSummaryResources.get(0);
        assertEquals(expectedIds[0].longValue(), first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedInnovationAreas[0], first.getInnovationArea());
        assertEquals(expectedCompetitionIds[0], first.getCompetitionId());
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations1, first.getPartnerOrganisations());
        assertEquals(expectedCompetitionStatuses[0], first.getCompetitionStatus());

        ApplicationAssessmentSummaryResource second = applicationAssessmentSummaryResources.get(1);
        assertEquals(expectedIds[1].longValue(), second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedInnovationAreas[1], second.getInnovationArea());
        assertEquals(expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisation());
        assertEquals(expectedPartnerOrganisations2, second.getPartnerOrganisations());
        assertEquals(expectedCompetitionStatuses[1], second.getCompetitionStatus());
    }

}