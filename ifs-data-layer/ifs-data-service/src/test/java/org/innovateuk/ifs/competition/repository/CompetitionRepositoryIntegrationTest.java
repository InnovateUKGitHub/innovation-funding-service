package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;

public class CompetitionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionRepository> {


    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testFundedAndInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        Assert.assertEquals(1L, repository.countProjectSetup().longValue());
        Assert.assertEquals(1, repository.findProjectSetup().size());
        Assert.assertEquals(compFundedAndInformed.getId().longValue(), repository.findProjectSetup().get(0).getId().longValue());
    }

    @Test
    public void testMultipleFundedAndInformed() {
        Competition compWithFeedBackReleased = newCompetition().withName("Comp1").withNonIfs(false).withSetupComplete(true).build();
        compWithFeedBackReleased = repository.save(compWithFeedBackReleased);

        Application applicationFeedbackReleased = newApplication().withCompetition(compWithFeedBackReleased).withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFeedbackReleased);

        Competition compFundedAndInformed = newCompetition().withName("Comp2").withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        Competition compNonIfs = newCompetition().withName("Comp3").withNonIfs(true).withSetupComplete(true).build();
        repository.save(compNonIfs);

        Assert.assertEquals(2L, repository.countProjectSetup().longValue());
        List<Competition> competitions = repository.findProjectSetup();
        Assert.assertEquals(2, competitions.size());
        Assert.assertTrue(competitions.get(0).getName().equals("Comp2") && competitions.get(1).getName().equals("Comp1") || competitions.get(1).getName().equals("Comp2") && competitions.get(0).getName().equals("Comp1"));
    }

}
