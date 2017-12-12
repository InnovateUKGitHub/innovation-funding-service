package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.competition.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.junit.Before;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;

import static org.junit.Assert.*;

public class CompetitionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Override
    protected void setRepository(CompetitionRepository repository) {
        this.repository = repository;
    }

    private Long org1Id;
    private Long org2Id;
    private List<Competition> existingSearchResults;

    @Before
    public void setup() {
        Organisation org = organisationRepository.findOneByName("Org1");
        assertNotNull(org);
        org1Id = org.getId();
        org = organisationRepository.findOneByName("Org2");
        assertNotNull(org);
        org2Id = org.getId();

        existingSearchResults = repository.findAll();
    }

    @Test
    @Rollback
    public void testFundedAndInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(1L, repository.countProjectSetup().longValue());
        assertEquals(1, repository.findProjectSetup().size());
        assertEquals(compFundedAndInformed.getId().longValue(), repository.findProjectSetup().get(0).getId().longValue());
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

        assertEquals(2L, repository.countProjectSetup().longValue());
        List<Competition> competitions = repository.findProjectSetup();
        assertEquals(2, competitions.size());
        assertTrue(competitions.get(0).getName().equals("Comp2") && competitions.get(1).getName().equals("Comp1") || competitions.get(1).getName().equals("Comp2") && competitions.get(0).getName().equals("Comp1"));
    }

    @Test
    @Rollback
    public void testFundedAndNotInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup().size());
    }

    @Test
    @Rollback
    public void testNotFundedAndInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.UNFUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup().size());
    }

    private List<Milestone> replaceOpenDateMilestoneDate(List<Milestone> milestones, ZonedDateTime time) {
        Optional<Milestone> openDate = milestones.stream().filter(m -> m.getType().equals(MilestoneType.OPEN_DATE)).findFirst();
        List<Milestone> nonOpenDateMilestones = milestones.stream().filter(m -> !m.getType().equals(MilestoneType.OPEN_DATE)).collect(Collectors.toList());
        if(openDate.isPresent()) {
            openDate.get().setDate(time);
            nonOpenDateMilestones.add(openDate.get());
        }
        return nonOpenDateMilestones;
    }
    @Test
    @Rollback
    public void testSearch() {
        User leadTechnologist = getUserByEmail("steve.smith@empire.com");
        User notLeadTechnologist = getUserByEmail("pete.tom@egg.com");

        Competition openComp = new Competition(null, null, null,null,"openComp", null, null, null);
        openComp.setLeadTechnologist(leadTechnologist);
        openComp.setSetupComplete(true);
        openComp = repository.save(openComp);
        openComp.setMilestones(replaceOpenDateMilestoneDate(openComp.getMilestones(), ZonedDateTime.now().minusHours(5L)));
        openComp = repository.save(openComp);
        CompetitionAssessmentParticipant competitionParticipant = buildCompetitionParticipant(openComp, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition earliestOpenComp = new Competition(null, null, null,null,"earliestOpenComp", null, null, null);
        earliestOpenComp.setLeadTechnologist(leadTechnologist);
        earliestOpenComp.setSetupComplete(true);
        earliestOpenComp = repository.save(earliestOpenComp);
        earliestOpenComp.setMilestones(replaceOpenDateMilestoneDate(earliestOpenComp.getMilestones(), ZonedDateTime.now().minusDays(3L)));
        earliestOpenComp = repository.save(earliestOpenComp);
        competitionParticipant = buildCompetitionParticipant(earliestOpenComp, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition compWithNoInnovationLead = new Competition(null, null, null,null,"compWithNoInnovationLead", null, null, null);
        compWithNoInnovationLead.setLeadTechnologist(notLeadTechnologist);
        compWithNoInnovationLead.setSetupComplete(true);
        compWithNoInnovationLead = repository.save(compWithNoInnovationLead);
        compWithNoInnovationLead.setMilestones(replaceOpenDateMilestoneDate(compWithNoInnovationLead.getMilestones(), ZonedDateTime.now().minusHours(10L)));
        compWithNoInnovationLead = repository.save(compWithNoInnovationLead);
        competitionParticipant = buildCompetitionParticipant(compWithNoInnovationLead, notLeadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition compInPreparation = new Competition(null, null, null,null,"compInPreparation", null, null, null);
        compInPreparation.setLeadTechnologist(leadTechnologist);
        compInPreparation.setSetupComplete(false);
        compInPreparation = repository.save(compInPreparation);
        compInPreparation.setMilestones(replaceOpenDateMilestoneDate(compInPreparation.getMilestones(), ZonedDateTime.now().minusHours(20L)));
        compInPreparation = repository.save(compInPreparation);
        competitionParticipant = buildCompetitionParticipant(compInPreparation, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition compReadyToOpen = new Competition(null, null, null,null,"compReadyToOpen", null, null, null);
        compReadyToOpen.setLeadTechnologist(leadTechnologist);
        compReadyToOpen.setSetupComplete(true);
        compReadyToOpen = repository.save(compReadyToOpen);
        compReadyToOpen.setMilestones(replaceOpenDateMilestoneDate(compReadyToOpen.getMilestones(), ZonedDateTime.now().plusHours(12L)));
        compReadyToOpen = repository.save(compReadyToOpen);
        competitionParticipant = buildCompetitionParticipant(compReadyToOpen, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition compInInform = new Competition(null, null, null,null,"compInInform", null, null, null);
        compInInform.setLeadTechnologist(leadTechnologist);
        compInInform.setSetupComplete(true);
        compInInform = repository.save(compInInform);
        compInInform.setMilestones(replaceOpenDateMilestoneDate(compInInform.getMilestones(), ZonedDateTime.now().minusDays(1L).minusHours(12L)));
        compInInform = repository.save(compInInform);
        competitionParticipant = buildCompetitionParticipant(compInInform, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Competition compInProjectSetup = new Competition(null, null, null,null,"compInProjectSetup", null, null, null);
        compInProjectSetup.setLeadTechnologist(leadTechnologist);
        compInProjectSetup.setSetupComplete(true);
        compInProjectSetup = repository.save(compInProjectSetup);
        compInProjectSetup.setMilestones(replaceOpenDateMilestoneDate(compInProjectSetup.getMilestones(), ZonedDateTime.now().minusDays(2L)));
        compInProjectSetup = repository.save(compInProjectSetup);
        competitionParticipant = buildCompetitionParticipant(compInProjectSetup, leadTechnologist);
        competitionParticipantRepository.save(competitionParticipant);

        Milestone feedbackReleasedMilestoneInProjectSetup = newMilestone().withCompetition(compInProjectSetup).withType(MilestoneType.FEEDBACK_RELEASED).withDate(ZonedDateTime.now().minusDays(1L)).build();
        milestoneRepository.save(feedbackReleasedMilestoneInProjectSetup);

        Pageable pageable = new PageRequest(0, 40);

        Page<Competition> searchResults = repository.search("%o%", pageable);
        List<Competition> filteredSearchResults = searchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        assertEquals(7, filteredSearchResults.size());
        assertEquals("earliestOpenComp", filteredSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredSearchResults.get(1).getName());
        assertEquals("compInInform", filteredSearchResults.get(2).getName());
        assertEquals("compInPreparation", filteredSearchResults.get(3).getName());
        assertEquals("compWithNoInnovationLead", filteredSearchResults.get(4).getName());
        assertEquals("openComp", filteredSearchResults.get(5).getName());
        assertEquals("compReadyToOpen", filteredSearchResults.get(6).getName());

        Page<Competition> leadTechnologistSearchResults = repository.searchForLeadTechnologist("%o%", leadTechnologist.getId(), pageable);
        List<Competition> filteredLeadTechnologistSearchResults = leadTechnologistSearchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        assertEquals(4, filteredLeadTechnologistSearchResults.size());
        assertEquals("earliestOpenComp", filteredLeadTechnologistSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredLeadTechnologistSearchResults.get(1).getName());
        assertEquals("compInInform", filteredLeadTechnologistSearchResults.get(2).getName());
        assertEquals("openComp", filteredLeadTechnologistSearchResults.get(3).getName());

        Page<Competition> supportUserSearchResults = repository.searchForSupportUser("%o%", pageable);
        List<Competition> filteredSupportUserSearchResults = supportUserSearchResults.getContent().stream().filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect(Collectors.toList());
        assertEquals(6, filteredSupportUserSearchResults.size());
        assertEquals("earliestOpenComp", filteredSupportUserSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredSupportUserSearchResults.get(1).getName());
        assertEquals("compInInform", filteredSupportUserSearchResults.get(2).getName());
        assertEquals("compWithNoInnovationLead", filteredSupportUserSearchResults.get(3).getName());
        assertEquals("openComp", filteredSupportUserSearchResults.get(4).getName());
        assertEquals("compReadyToOpen", filteredSupportUserSearchResults.get(5).getName());
    }

    private CompetitionAssessmentParticipant buildCompetitionParticipant(Competition competition, User user){
        CompetitionAssessmentParticipant competitionParticipant = new CompetitionAssessmentParticipant();
        competitionParticipant.setUser(user);
        competitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
        competitionParticipant.setStatus(ParticipantStatus.ACCEPTED);
        competitionParticipant.setProcess(competition);
        return competitionParticipant;
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

    @Test
    public void testCountLiveForInnovationLead(){
        // TODO: Improve once IFS-2222 is done.
        long innovationLead1Id = 51L;
        long innovationLead2Id = 52L;

        Long count = repository.countLiveForInnovationLead(innovationLead2Id);
        assertEquals(new Long(1L), count);

        count = repository.countLiveForInnovationLead(innovationLead1Id);
        assertEquals(new Long(0L), count);
    }

    @Test
    public void findByApplicationsId() {
        Competition competition = repository.save(newCompetition().withId(7L).build());
        Application application = applicationRepository.save(newApplication().withId(11L).withCompetition(competition).build());

        Competition retrieved = repository.findByApplicationsId(application.getId());

        assertEquals(competition, retrieved);
    }

    // IFS-2263 -- ensure milestone dates aren't rounded up
    @Test
    @Rollback
    public void testMilestoneDatesTruncated() {
        final ZonedDateTime dateTime = ZonedDateTime.parse("2017-12-03T10:18:30.500Z");
        final ZonedDateTime expectedDateTime = ZonedDateTime.parse("2017-12-03T10:18:30.000Z");

        Competition savedCompetition = repository.save(
                new Competition(null, null, null,null,"comp", dateTime, null, null)
        );

        flushAndClearSession();

        Competition retrievedCompetition = repository.findById(savedCompetition.getId());
        assertTrue(expectedDateTime.isEqual(retrievedCompetition.getStartDate()));
    }

    @Test
    public void findByProjectId() {
        Competition competition = repository.save(newCompetition().withId(7L).build());
        Application application = applicationRepository.save(newApplication().withId(17L).withCompetition(competition).build());
        Project project = projectRepository.save(newProject()
                .withId(17L)
                .withApplication(application)
                .withName("Project Name")
                .build()
        );

        Competition retrieved = repository.findByProjectId(project.getId());

        assertEquals(competition, retrieved);
    }

    @Test
    public void findByAssessmentId() {
        Competition competition = repository.save(newCompetition().withId(7L).build());
        Application application = applicationRepository.save(newApplication().withId(11L).withCompetition(competition).build());


        Assessment assessment = assessmentRepository.save(newAssessment()
                .withId(13L)
                .withApplication(application)
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION_ASSESSMENT, State.SUBMITTED))
                .build()
        );

        Competition retrieved = repository.findByAssessmentId(assessment.getId());

        assertEquals(competition, retrieved);
    }
}