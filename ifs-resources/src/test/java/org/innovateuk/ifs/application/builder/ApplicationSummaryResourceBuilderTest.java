package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNDECIDED;
import static org.junit.Assert.assertEquals;

public class ApplicationSummaryResourceBuilderTest {

    private Long[] ids = {1L, 2L};
    private String[] names = {"Application 1", "Application 2"};
    private String[] leads = {"Lead 1", "Lead 2"};
    private String[] leadApplicants = {"Lead Applicant 1", "Lead Applicant 2"};
    private String[] statuses = {"Submitted", "Started"};
    private Integer[] completedPercentages = {100, 50};
    private Integer[] numberOfPartners = {5, 4};
    private BigDecimal[] grantsRequested = {BigDecimal.valueOf(1000), BigDecimal.valueOf(2000)};
    private BigDecimal[] totalProjectCosts = {BigDecimal.valueOf(10000), BigDecimal.valueOf(20000)};
    private Long[] durations = {200L, 400L};
    private FundingDecision[] fundingDecisions = {FUNDED, UNDECIDED};
    private String[] innovationAreas = {"Innovation Area 1", "Innovation Area 2"};
    private Boolean[] ineligibleInformeds = { true, false};

    @Test
    public void buildOne() throws Exception {
        ApplicationSummaryResource summaryResource = newApplicationSummaryResource()
                .withId(ids[0])
                .withName(names[0])
                .withLead(leads[0])
                .withLeadApplicant(leadApplicants[0])
                .withStatus(statuses[0])
                .withCompletedPercentage(completedPercentages[0])
                .withNumberOfPartners(numberOfPartners[0])
                .withGrantRequested(grantsRequested[0])
                .withTotalProjectCost(totalProjectCosts[0])
                .withDuration(durations[0])
                .withFundingDecision(fundingDecisions[0])
                .withInnovationArea(innovationAreas[0])
                .withIneligibleInformed(ineligibleInformeds[0])
                .build();

        assertEquals(ids[0].longValue(), summaryResource.getId());
        assertEquals(names[0], summaryResource.getName());
        assertEquals(leads[0], summaryResource.getLead());
        assertEquals(leadApplicants[0], summaryResource.getLeadApplicant());
        assertEquals(statuses[0], summaryResource.getStatus());
        assertEquals(completedPercentages[0].intValue(), summaryResource.getCompletedPercentage());
        assertEquals(numberOfPartners[0].intValue(), summaryResource.getNumberOfPartners());
        assertEquals(grantsRequested[0], summaryResource.getGrantRequested());
        assertEquals(totalProjectCosts[0], summaryResource.getTotalProjectCost());
        assertEquals(durations[0].longValue(), summaryResource.getDuration());
        assertEquals(fundingDecisions[0], summaryResource.getFundingDecision());
        assertEquals(innovationAreas[0], summaryResource.getInnovationArea());
        assertEquals(ineligibleInformeds[0], summaryResource.isIneligibleInformed());
    }

    @Test
    public void buildMany() throws Exception {
        List<ApplicationSummaryResource> summaryResources = newApplicationSummaryResource()
                .withId(ids)
                .withName(names)
                .withLead(leads)
                .withLeadApplicant(leadApplicants)
                .withStatus(statuses)
                .withCompletedPercentage(completedPercentages)
                .withNumberOfPartners(numberOfPartners)
                .withGrantRequested(grantsRequested)
                .withTotalProjectCost(totalProjectCosts)
                .withDuration(durations)
                .withFundingDecision(fundingDecisions)
                .withInnovationArea(innovationAreas)
                .withIneligibleInformed(ineligibleInformeds)
                .build(2);

        assertEquals(ids[0].longValue(), summaryResources.get(0).getId());
        assertEquals(names[0], summaryResources.get(0).getName());
        assertEquals(leads[0], summaryResources.get(0).getLead());
        assertEquals(leadApplicants[0], summaryResources.get(0).getLeadApplicant());
        assertEquals(statuses[0], summaryResources.get(0).getStatus());
        assertEquals(completedPercentages[0].intValue(), summaryResources.get(0).getCompletedPercentage());
        assertEquals(numberOfPartners[0].intValue(), summaryResources.get(0).getNumberOfPartners());
        assertEquals(grantsRequested[0], summaryResources.get(0).getGrantRequested());
        assertEquals(totalProjectCosts[0], summaryResources.get(0).getTotalProjectCost());
        assertEquals(durations[0].longValue(), summaryResources.get(0).getDuration());
        assertEquals(fundingDecisions[0], summaryResources.get(0).getFundingDecision());
        assertEquals(innovationAreas[0], summaryResources.get(0).getInnovationArea());
        assertEquals(ineligibleInformeds[0], summaryResources.get(0).isIneligibleInformed());

        assertEquals(ids[1].longValue(), summaryResources.get(1).getId());
        assertEquals(names[1], summaryResources.get(1).getName());
        assertEquals(leads[1], summaryResources.get(1).getLead());
        assertEquals(leadApplicants[1], summaryResources.get(1).getLeadApplicant());
        assertEquals(statuses[1], summaryResources.get(1).getStatus());
        assertEquals(completedPercentages[1].intValue(), summaryResources.get(1).getCompletedPercentage());
        assertEquals(numberOfPartners[1].intValue(), summaryResources.get(1).getNumberOfPartners());
        assertEquals(grantsRequested[1], summaryResources.get(1).getGrantRequested());
        assertEquals(totalProjectCosts[1], summaryResources.get(1).getTotalProjectCost());
        assertEquals(durations[1].longValue(), summaryResources.get(1).getDuration());
        assertEquals(fundingDecisions[1], summaryResources.get(1).getFundingDecision());
        assertEquals(innovationAreas[1], summaryResources.get(1).getInnovationArea());
        assertEquals(ineligibleInformeds[1], summaryResources.get(1).isIneligibleInformed());
    }
}
