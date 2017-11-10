package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;

import static org.junit.Assert.*;

public class CompetitionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionRepository repository) {
        this.repository = repository;
    }

    private Long org1Id;
    private Long org2Id;
    private List<Competition> existingSearchResults;
    private long compId;

    @Before
    public void setup() {
        Organisation org = organisationRepository.findOneByName("Org1");
        assertNotNull(org);
        org1Id = org.getId();
        org = organisationRepository.findOneByName("Org2");
        assertNotNull(org);
        org2Id = org.getId();

        // don't want to clash with integration test data loaded via SQL scripts
        compId = repository.findAll().stream().max((c1, c2) -> c1.getId().compareTo(c2.getId())).get().getId();

        existingSearchResults = repository.findAll();
    }

    @Test
    @Rollback
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
    @Rollback
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

    @Test
    @Rollback
    public void testFundedAndNotInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).build();
        applicationRepository.save(applicationFundedAndInformed);

        Assert.assertEquals(0L, repository.countProjectSetup().longValue());
        Assert.assertEquals(0, repository.findProjectSetup().size());
    }

    @Test
    @Rollback
    public void testNotFundedAndInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.UNFUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        Assert.assertEquals(0L, repository.countProjectSetup().longValue());
        Assert.assertEquals(0, repository.findProjectSetup().size());
    }

    @Test
    @Rollback
    public void testSearch() {
        User leadTechnologist = getUserByEmail("steve.smith@empire.com");
        User notLeadTechnologist = getUserByEmail("pete.tom@egg.com");

        Competition openComp = newCompetition().withName("openComp").withLeadTechnologist(leadTechnologist).withSetupComplete(true).withId(++compId).build();
        openComp = repository.save(openComp);
        Milestone openDateMilestone = newMilestone().withCompetition(openComp).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusHours(5L)).build();
        milestoneRepository.save(openDateMilestone);

        Competition earliestOpenComp = newCompetition().withName("earliestOpenComp").withLeadTechnologist(leadTechnologist).withSetupComplete(true).withId(++compId).build();
        earliestOpenComp = repository.save(earliestOpenComp);
        Milestone earlierOpenDateMilestone = newMilestone().withCompetition(earliestOpenComp).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusDays(3L)).build();
        milestoneRepository.save(earlierOpenDateMilestone);

        Competition compWithNoInnovationLead = newCompetition().withName("compWithNoInnovationLead").withSetupComplete(true).withLeadTechnologist(notLeadTechnologist).withId(++compId).build();
        compWithNoInnovationLead = repository.save(compWithNoInnovationLead);
        Milestone openDateMilestoneNoInnovationLead = newMilestone().withCompetition(compWithNoInnovationLead).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusHours(10L)).build();
        milestoneRepository.save(openDateMilestoneNoInnovationLead);

        Competition compInPreparation = newCompetition().withName("compInPreparation").withSetupComplete(false).withLeadTechnologist(leadTechnologist).withId(++compId).build();
        compInPreparation = repository.save(compInPreparation);
        Milestone openDateMilestoneInPreparation = newMilestone().withCompetition(compInPreparation).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusHours(20L)).build();
        milestoneRepository.save(openDateMilestoneInPreparation);

        Competition compReadyToOpen = newCompetition().withName("compReadyToOpen").withSetupComplete(true).withLeadTechnologist(leadTechnologist).withId(++compId).build();
        compReadyToOpen = repository.save(compReadyToOpen);
        Milestone openDateMilestoneReadyToOpen = newMilestone().withCompetition(compReadyToOpen).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().plusHours(12L)).build();
        milestoneRepository.save(openDateMilestoneReadyToOpen);

        Competition compInInform = newCompetition().withName("compInInform").withSetupComplete(true).withLeadTechnologist(leadTechnologist).withId(++compId).build();
        compInInform = repository.save(compInInform);
        Milestone openDateMilestoneInInform = newMilestone().withCompetition(compInInform).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusDays(1L).minusHours(12L)).build();
        milestoneRepository.save(openDateMilestoneInInform);

        Competition compInProjectSetup = newCompetition().withName("compInProjectSetup").withSetupComplete(true).withLeadTechnologist(leadTechnologist).withId(++compId).build();
        compInProjectSetup = repository.save(compInProjectSetup);
        Milestone openDateMilestoneInProjectSetup = newMilestone().withCompetition(compInProjectSetup).withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now().minusDays(2L)).build();
        milestoneRepository.save(openDateMilestoneInProjectSetup);
        Milestone feedbackReleasedMilestoneInProjectSetup = newMilestone().withCompetition(compInProjectSetup).withType(MilestoneType.FEEDBACK_RELEASED).withDate(ZonedDateTime.now().minusDays(1L)).build();
        milestoneRepository.save(feedbackReleasedMilestoneInProjectSetup);

        Pageable pageable = new PageRequest(0, 40);

        Page<Competition> searchResults = repository.search("%o%", pageable);
        List<Competition> filteredSearchResults = searchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        Assert.assertEquals(7, filteredSearchResults.size());
        Assert.assertEquals("earliestOpenComp", filteredSearchResults.get(0).getName());
        Assert.assertEquals("compInProjectSetup", filteredSearchResults.get(1).getName());
        Assert.assertEquals("compInInform", filteredSearchResults.get(2).getName());
        Assert.assertEquals("compInPreparation", filteredSearchResults.get(3).getName());
        Assert.assertEquals("compWithNoInnovationLead", filteredSearchResults.get(4).getName());
        Assert.assertEquals("openComp", filteredSearchResults.get(5).getName());
        Assert.assertEquals("compReadyToOpen", filteredSearchResults.get(6).getName());

        Page<Competition> leadTechnologistSearchResults = repository.searchForLeadTechnologist("%o%", leadTechnologist.getId(), pageable);
        List<Competition> filteredLeadTechnologistSearchResults = leadTechnologistSearchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        Assert.assertEquals(3, filteredLeadTechnologistSearchResults.size());
        Assert.assertEquals("earliestOpenComp", filteredLeadTechnologistSearchResults.get(0).getName());
        Assert.assertEquals("compInInform", filteredLeadTechnologistSearchResults.get(1).getName());
        Assert.assertEquals("openComp", filteredLeadTechnologistSearchResults.get(2).getName());

        Page<Competition> supportUserSearchResults = repository.searchForSupportUser("%o%", pageable);
        List<Competition> filteredSupportUserSearchResults = supportUserSearchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        Assert.assertEquals(6, filteredSupportUserSearchResults.size());
        Assert.assertEquals("earliestOpenComp", filteredSupportUserSearchResults.get(0).getName());
        Assert.assertEquals("compInProjectSetup", filteredSupportUserSearchResults.get(1).getName());
        Assert.assertEquals("compInInform", filteredSupportUserSearchResults.get(2).getName());
        Assert.assertEquals("compWithNoInnovationLead", filteredSupportUserSearchResults.get(3).getName());
        Assert.assertEquals("openComp", filteredSupportUserSearchResults.get(4).getName());
        Assert.assertEquals("compReadyToOpen", filteredSupportUserSearchResults.get(5).getName());
    }

    @Test
    public void oneQueryCreatedByProjectFinance() {
        List<Competition> comps = repository.findByName("Comp21001");
        assertTrue(comps.size() > 0);
        assertEquals(0L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(0L, results.size());
    }

    @Test
    public void oneQueryCreatedByProjectManager() {
        List<Competition> comps = repository.findByName("Comp21002");
        assertTrue(comps.size() > 0);
        assertEquals(1L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(1L, results.size());
        List<Application> apps = applicationRepository.findByName("App21002");
        assertEquals(1L, apps.size());
        Long appId = apps.get(0).getId();
        List<Project> projects = projectRepository.findByApplicationCompetitionId(comps.get(0).getId());
        Project project = projects.stream().filter(p -> p.getName().equals("project 2")).findFirst().get();
        assertNotNull(project);
        Long projectId = project.getId();
        assertEquals(new CompetitionOpenQueryResource(appId, org2Id, "Org2", projectId, "project 2"), results.get(0));
    }

    @Test
    public void oneQueryCreatedByProjectFinanceWithResponseFromProjectManager() {
        List<Competition> comps = repository.findByName("Comp21003");
        assertTrue(comps.size() > 0);
        assertEquals(1L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(1L, results.size());
        List<Application> apps = applicationRepository.findByName("App21003");
        assertEquals(1L, apps.size());
        Long appId = apps.get(0).getId();
        List<Project> projects = projectRepository.findByApplicationCompetitionId(comps.get(0).getId());
        Project project = projects.stream().filter(p -> p.getName().equals("project 3")).findFirst().get();
        assertNotNull(project);
        Long projectId = project.getId();
        assertEquals(new CompetitionOpenQueryResource(appId, org1Id, "Org1", projectId, "project 3"), results.get(0));
    }

    @Test
    public void twoOpenQueryResponsesFromDifferentPartners() {
        List<Competition> comps = repository.findByName("Comp21005");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(2L, results.size());
        List<Application> apps = applicationRepository.findByName("App21005");
        assertEquals(1L, apps.size());
        Long appId = apps.get(0).getId();
        List<Project> projects = projectRepository.findByApplicationCompetitionId(comps.get(0).getId());
        Project project = projects.stream().filter(p -> p.getName().equals("project 5")).findFirst().get();
        assertNotNull(project);
        Long projectId = project.getId();
        assertEquals(new CompetitionOpenQueryResource(appId, org1Id, "Org1", projectId, "project 5"), results.get(0));
        assertEquals(new CompetitionOpenQueryResource(appId, org2Id, "Org2", projectId, "project 5"), results.get(1));
    }

    @Test
    public void twoProjectsHaveOpenQueries() {
        List<Competition> comps = repository.findByName("Comp21006");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(2L, results.size());
        List<Application> apps = applicationRepository.findByName("App21006a");
        assertEquals(1L, apps.size());
        Long appId = apps.get(0).getId();
        List<Project> projects = projectRepository.findByApplicationCompetitionId(comps.get(0).getId());
        Project project = projects.stream().filter(p -> p.getName().equals("project 6")).findFirst().get();
        assertNotNull(project);
        Long projectId = project.getId();
        assertEquals(new CompetitionOpenQueryResource(appId, org1Id, "Org1", projectId, "project 6"), results.get(0));

        apps = applicationRepository.findByName("App21006b");
        assertEquals(1L, apps.size());
        appId = apps.get(0).getId();
        project = projects.stream().filter(p -> p.getName().equals("project 7")).findFirst().get();
        assertNotNull(project);
        projectId = project.getId();
        assertEquals(new CompetitionOpenQueryResource(appId, org1Id, "Org1", projectId, "project 7"), results.get(1));
    }

    @Test
    public void oneQueryCreatedByProjectFinanceWithResponseFromProjectManagerButWithSpendProfileGenerated() {
        List<Competition> comps = repository.findByName("Comp21007");
        assertTrue(comps.size() > 0);
        assertEquals(0L, repository.countOpenQueries(comps.get(0).getId()).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetition(comps.get(0).getId());
        assertEquals(0L, results.size());
    }
}
