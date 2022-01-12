package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;

public class CompetitionSummaryResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        long competitionId = 1L;
        String competitionName = "Competition Name";
        CompetitionStatus competitionStatus = IN_ASSESSMENT;
        ZonedDateTime applicationDeadline = now();
        int totalNumberOfApplications = 100;
        int applicationsStarted = 20;
        int applicationsInProgress = 30;
        int applicationsSubmitted = 40;
        int applicationsNotSubmitted = 50;
        int applicationsFunded = 10;
        int ineligibleApplications = 5;
        int assessorsInvited = 20;

        CompetitionSummaryResource summaryResource = newCompetitionSummaryResource()
                .withId(competitionId)
                .withCompetitionName(competitionName)
                .withCompetitionStatus(competitionStatus)
                .withApplicationsStarted(applicationsStarted)
                .withApplicationDeadline(applicationDeadline)
                .withTotalNumberOfApplications(totalNumberOfApplications)
                .withApplicationsInProgress(applicationsInProgress)
                .withApplicationsSubmitted(applicationsSubmitted)
                .withApplicationsNotSubmitted(applicationsNotSubmitted)
                .withApplicationsFunded(applicationsFunded)
                .withIneligibleApplications(ineligibleApplications)
                .withAssesorsInvited(assessorsInvited)
                .build();

        assertEquals(competitionId, summaryResource.getCompetitionId());
        assertEquals(competitionName, summaryResource.getCompetitionName());
        assertEquals(competitionStatus, summaryResource.getCompetitionStatus());
        assertEquals(applicationsStarted, summaryResource.getApplicationsStarted());
        assertEquals(applicationDeadline, summaryResource.getApplicationDeadline());
        assertEquals(totalNumberOfApplications, summaryResource.getTotalNumberOfApplications());
        assertEquals(applicationsInProgress, summaryResource.getApplicationsInProgress());
        assertEquals(applicationsSubmitted, summaryResource.getApplicationsSubmitted());
        assertEquals(applicationsNotSubmitted, summaryResource.getApplicationsNotSubmitted());
        assertEquals(applicationsFunded, summaryResource.getApplicationsFunded());
        assertEquals(ineligibleApplications, summaryResource.getIneligibleApplications());
        assertEquals(assessorsInvited, summaryResource.getAssessorsInvited());
    }

    @Test
    public void buildMany() throws Exception {
        long[] competitionId = {1L, 2L};
        String[] competitionName = {"Competition Name 1", "Competition Name 2"};
        CompetitionStatus[] competitionStatus = {IN_ASSESSMENT, CLOSED};
        ZonedDateTime[] applicationDeadline = {now().minusDays(1L), now().minusDays(5L)};
        int[] totalNumberOfApplications = {100, 200};
        int[] applicationsStarted = {20, 40};
        int[] applicationsInProgress = {30, 60};
        int[] applicationsSubmitted = {40, 80};
        int[] applicationsNotSubmitted = {50, 100};
        int[] applicationsFunded = {10, 20};
        int[] ineligibleApplications = {5, 10};
        int[] assessorsInvited = {20, 40};

        List<CompetitionSummaryResource> summaryResources = newCompetitionSummaryResource()
                .withId(competitionId[0], competitionId[1])
                .withCompetitionName(competitionName[0], competitionName[1])
                .withCompetitionStatus(competitionStatus[0], competitionStatus[1])
                .withApplicationsStarted(applicationsStarted[0], applicationsStarted[1])
                .withApplicationDeadline(applicationDeadline[0], applicationDeadline[1])
                .withTotalNumberOfApplications(totalNumberOfApplications[0], totalNumberOfApplications[1])
                .withApplicationsInProgress(applicationsInProgress[0], applicationsInProgress[1])
                .withApplicationsSubmitted(applicationsSubmitted[0], applicationsSubmitted[1])
                .withApplicationsNotSubmitted(applicationsNotSubmitted[0], applicationsNotSubmitted[1])
                .withApplicationsFunded(applicationsFunded[0], applicationsFunded[1])
                .withIneligibleApplications(ineligibleApplications[0], ineligibleApplications[1])
                .withAssesorsInvited(assessorsInvited[0], assessorsInvited[1])
                .build(2);

        CompetitionSummaryResource summaryResource1 = summaryResources.get(0);
        assertEquals(competitionId[0], summaryResource1.getCompetitionId());
        assertEquals(competitionName[0], summaryResource1.getCompetitionName());
        assertEquals(competitionStatus[0], summaryResource1.getCompetitionStatus());
        assertEquals(applicationsStarted[0], summaryResource1.getApplicationsStarted());
        assertEquals(applicationDeadline[0], summaryResource1.getApplicationDeadline());
        assertEquals(totalNumberOfApplications[0], summaryResource1.getTotalNumberOfApplications());
        assertEquals(applicationsInProgress[0], summaryResource1.getApplicationsInProgress());
        assertEquals(applicationsSubmitted[0], summaryResource1.getApplicationsSubmitted());
        assertEquals(applicationsNotSubmitted[0], summaryResource1.getApplicationsNotSubmitted());
        assertEquals(applicationsFunded[0], summaryResource1.getApplicationsFunded());
        assertEquals(ineligibleApplications[0], summaryResource1.getIneligibleApplications());
        assertEquals(assessorsInvited[0], summaryResource1.getAssessorsInvited());

        CompetitionSummaryResource summaryResource2 = summaryResources.get(1);
        assertEquals(competitionId[1], summaryResource2.getCompetitionId());
        assertEquals(competitionName[1], summaryResource2.getCompetitionName());
        assertEquals(competitionStatus[1], summaryResource2.getCompetitionStatus());
        assertEquals(applicationsStarted[1], summaryResource2.getApplicationsStarted());
        assertEquals(applicationDeadline[1], summaryResource2.getApplicationDeadline());
        assertEquals(totalNumberOfApplications[1], summaryResource2.getTotalNumberOfApplications());
        assertEquals(applicationsInProgress[1], summaryResource2.getApplicationsInProgress());
        assertEquals(applicationsSubmitted[1], summaryResource2.getApplicationsSubmitted());
        assertEquals(applicationsNotSubmitted[1], summaryResource2.getApplicationsNotSubmitted());
        assertEquals(applicationsFunded[1], summaryResource2.getApplicationsFunded());
        assertEquals(ineligibleApplications[1], summaryResource2.getIneligibleApplications());
        assertEquals(assessorsInvited[1], summaryResource2.getAssessorsInvited());
    }
}
