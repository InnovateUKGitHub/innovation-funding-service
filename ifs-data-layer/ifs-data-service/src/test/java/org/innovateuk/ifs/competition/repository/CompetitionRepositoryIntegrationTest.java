package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.user.domain.User;
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

        assertEquals(1L, repository.countProjectSetup().longValue());
        assertEquals(1, repository.findProjectSetup().size());
        assertEquals(compFundedAndInformed.getId().longValue(), repository.findProjectSetup().get(0).getId().longValue());
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

        assertEquals(2L, repository.countProjectSetup().longValue());
        List<Competition> competitions = repository.findProjectSetup();
        assertEquals(2, competitions.size());
        assertTrue(competitions.get(0).getName().equals("Comp2") && competitions.get(1).getName().equals("Comp1") || competitions.get(1).getName().equals("Comp2") && competitions.get(0).getName().equals("Comp1"));
    }

    @Test
    public void testFundedAndNotInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.FUNDED).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup().size());
    }

    @Test
    public void testNotFundedAndInformed() {
        Competition compFundedAndInformed = newCompetition().withNonIfs(false).withSetupComplete(true).build();
        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed).withFundingDecision(FundingDecisionStatus.UNFUNDED).withManageFundingEmailDate(ZonedDateTime.now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup().size());
    }

    @Test
    @Rollback
    public void testSearch() {
        flushAndClearSession();
        User leadTechnologist = getUserByEmail("steve.smith@empire.com");
        User notLeadTechnologist = getUserByEmail("pete.tom@egg.com");

        // don't want to clash with integration test data loaded via SQL scripts
        long compId = repository.findAll().stream().max((c1, c2) -> c1.getId().compareTo(c2.getId())).get().getId();

        // find any pre-existing competitions that would match, so we can ignore them when checking results
        List<Competition> existingSearchResults = repository.findAll().stream()
                .filter(c -> ((c.getName() != null && c.getName().contains("o"))
                            || (c.getCompetitionType() != null && c.getCompetitionType().getName().contains("o")))
                        && !c.isTemplate()
                        && !c.isNonIfs()
                        && c.getMilestones().stream().filter(m -> m.getType() == null || m.getType().equals(MilestoneType.OPEN_DATE)).count() >= 1).collect(Collectors.toList());

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

        flushAndClearSession();

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
        assertEquals(3, filteredLeadTechnologistSearchResults.size());
        assertEquals("earliestOpenComp", filteredLeadTechnologistSearchResults.get(0).getName());
        assertEquals("compInInform", filteredLeadTechnologistSearchResults.get(1).getName());
        assertEquals("openComp", filteredLeadTechnologistSearchResults.get(2).getName());

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

    @Test
    public void findByApplicationsId() {
        Competition competition = repository.save(newCompetition().withId(7L).build());
        Application application = applicationRepository.save(newApplication().withId(11L).withCompetition(competition).build());

        Competition retrieved = repository.findByApplicationsId(application.getId());

        assertEquals(competition, retrieved);
    }

    @Test
    public void findByProjectId() {
        Competition competition = repository.save(newCompetition().withId(7L).build());
        Application application = applicationRepository.save(newApplication().withId(11L).withCompetition(competition).build());
        Project project = projectRepository.save(newProject()
                .withId(13L)
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