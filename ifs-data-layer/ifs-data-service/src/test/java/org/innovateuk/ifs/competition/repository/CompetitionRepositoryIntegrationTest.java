package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
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
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private UserRepository userRepository;

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
    public void fundedAndInformed() {

        loginCompAdmin();

        Competition compFundedAndInformed = newCompetition()
                .withId()
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        compFundedAndInformed = repository.save(compFundedAndInformed);

        Milestone feedbackReleasedMilestone =
                new Milestone(MilestoneType.FEEDBACK_RELEASED, ZonedDateTime.now().minusDays(1), compFundedAndInformed);

        milestoneRepository.save(feedbackReleasedMilestone);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed)
                .withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        // as the Competition is deemed closed when it is in Project Setup, it will appear in the Project Setup
        // Competition list
        assertEquals(1L, repository.countProjectSetup().longValue());
        assertEquals(1, repository.findProjectSetup(PageRequest.of(0, 10)).getTotalElements());

        // and will also appear in the Previous competitions list
        assertEquals(1L, repository.countPrevious().longValue());
        assertEquals(1, repository.findPrevious(PageRequest.of(0, 10)).getTotalElements());

        assertEquals(compFundedAndInformed.getId().longValue(), repository.findProjectSetup(PageRequest.of(0, 10)).getContent().get(0).getId()
                .longValue());
    }

    @Test
    @Rollback
    public void multipleFundedAndInformed() {

        loginCompAdmin();

        CompetitionType competitionType = newCompetitionType()
                .withId(1L)
                .withName("Programme")
                .build();

        Competition compWithFeedBackReleased = newCompetition()
                .withId()
                .withName("Comp1")
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompetitionType(competitionType)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        compWithFeedBackReleased = repository.save(compWithFeedBackReleased);

        Application applicationFeedbackReleased = newApplication().withCompetition(compWithFeedBackReleased)
                .withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(now()).build();
        applicationRepository.save(applicationFeedbackReleased);

        Competition compFundedAndInformed = newCompetition()
                .withName("Comp2")
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompetitionType(competitionType)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed)
                .withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        Competition compNonIfs = newCompetition().withName("Comp3").withNonIfs(true).withSetupComplete(true).build();
        repository.save(compNonIfs);

        assertEquals(2L, repository.countProjectSetup().longValue());
        List<Competition> competitions = repository.findProjectSetup(PageRequest.of(0, 10)).getContent();
        assertEquals(2, competitions.size());
        assertTrue(competitions.get(0).getName().equals("Comp2") && competitions.get(1).getName().equals("Comp1") ||
                competitions.get(1).getName().equals("Comp2") && competitions.get(0).getName().equals("Comp1"));
    }

    @Test
    @Rollback
    public void fundedAndInformedWithReleaseFeedbackCompletionStage() {

        loginCompAdmin();

        Competition compFundedAndInformed = newCompetition()
                .withId()
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .build();

        compFundedAndInformed = repository.save(compFundedAndInformed);

        Milestone feedbackReleasedMilestone =
                new Milestone(MilestoneType.FEEDBACK_RELEASED, ZonedDateTime.now().minusDays(1), compFundedAndInformed);

        milestoneRepository.save(feedbackReleasedMilestone);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed)
                .withFundingDecision(FundingDecisionStatus.FUNDED).withManageFundingEmailDate(now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        // when a Competition is considered closed at the Release Feedback stage, the Competition does not show
        // in Project Setup state but just Previous state
        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup(PageRequest.of(0, 10)).getTotalElements());

        assertEquals(1L, repository.countPrevious().longValue());
        assertEquals(1, repository.findPrevious(PageRequest.of(0, 10)).getTotalElements());

        assertEquals(compFundedAndInformed.getId().longValue(), repository.findPrevious(PageRequest.of(0, 10)).getContent().get(0).getId()
                .longValue());
    }

    @Test
    @Rollback
    public void fundedAndNotInformed() {
        loginCompAdmin();

        Competition compFundedAndInformed = newCompetition()
                .withId()
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed)
                .withFundingDecision(FundingDecisionStatus.FUNDED).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup(PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    @Rollback
    public void notFundedAndInformed() {
        loginCompAdmin();

        Competition compFundedAndInformed = newCompetition()
                .withId()
                .withNonIfs(false)
                .withSetupComplete(true)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        compFundedAndInformed = repository.save(compFundedAndInformed);

        Application applicationFundedAndInformed = newApplication().withCompetition(compFundedAndInformed)
                .withFundingDecision(FundingDecisionStatus.UNFUNDED).withManageFundingEmailDate(now()).build();
        applicationRepository.save(applicationFundedAndInformed);

        assertEquals(0L, repository.countProjectSetup().longValue());
        assertEquals(0, repository.findProjectSetup(PageRequest.of(0, 10)).getTotalElements());
    }

    private List<Milestone> replaceOpenDateMilestoneDate(List<Milestone> milestones, ZonedDateTime time) {
        Optional<Milestone> openDate = milestones.stream().filter(m -> m.getType().equals(MilestoneType.OPEN_DATE))
                .findFirst();
        List<Milestone> nonOpenDateMilestones = milestones.stream().filter(m -> !m.getType().equals(MilestoneType
                .OPEN_DATE)).collect(Collectors.toList());
        if (openDate.isPresent()) {
            openDate.get().setDate(time);
            nonOpenDateMilestones.add(openDate.get());
        }
        return nonOpenDateMilestones;
    }

    @Test
    @Rollback
    public void search() {
        User leadTechnologist = getUserByEmail("steve.smith@empire.com");
        User notLeadTechnologist = getUserByEmail("pete.tom@egg.com");

        GrantTermsAndConditions termsAndConditions = new GrantTermsAndConditions();
        termsAndConditions.setId(1L);

        Competition openComp = new Competition(null, null, "openComp", null, null, null, termsAndConditions);
        openComp.setTermsAndConditions(termsAndConditions);

        openComp.setLeadTechnologist(leadTechnologist);
        openComp.setSetupComplete(true);
        openComp = repository.save(openComp);
        openComp.setMilestones(replaceOpenDateMilestoneDate(openComp.getMilestones(), now().minusHours(5L)));
        openComp = repository.save(openComp);
        AssessmentParticipant competitionParticipant = buildCompetitionParticipant(openComp, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition earliestOpenComp = new Competition(null, null, "earliestOpenComp", null, null, null,
                termsAndConditions);
        earliestOpenComp.setLeadTechnologist(leadTechnologist);
        earliestOpenComp.setSetupComplete(true);
        earliestOpenComp = repository.save(earliestOpenComp);
        earliestOpenComp.setMilestones(replaceOpenDateMilestoneDate(earliestOpenComp.getMilestones(), now().minusDays
                (3L)));
        earliestOpenComp = repository.save(earliestOpenComp);
        competitionParticipant = buildCompetitionParticipant(earliestOpenComp, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition compWithNoInnovationLead = new Competition(null, null, "compWithNoInnovationLead", null,
                null, null, termsAndConditions);
        compWithNoInnovationLead.setLeadTechnologist(notLeadTechnologist);
        compWithNoInnovationLead.setSetupComplete(true);
        compWithNoInnovationLead = repository.save(compWithNoInnovationLead);
        compWithNoInnovationLead.setMilestones(replaceOpenDateMilestoneDate(compWithNoInnovationLead.getMilestones(),
                now().minusHours(10L)));
        compWithNoInnovationLead = repository.save(compWithNoInnovationLead);
        competitionParticipant = buildCompetitionParticipant(compWithNoInnovationLead, notLeadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition compInPreparation = new Competition(null, null, "compInPreparation", null, null, null,
                termsAndConditions);
        compInPreparation.setLeadTechnologist(leadTechnologist);
        compInPreparation.setSetupComplete(false);
        compInPreparation = repository.save(compInPreparation);
        compInPreparation.setMilestones(replaceOpenDateMilestoneDate(compInPreparation.getMilestones(), now()
                .minusHours(20L)));
        compInPreparation = repository.save(compInPreparation);
        competitionParticipant = buildCompetitionParticipant(compInPreparation, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition compReadyToOpen = new Competition(null, null, "compReadyToOpen", null, null, null,
                termsAndConditions);
        compReadyToOpen.setLeadTechnologist(leadTechnologist);
        compReadyToOpen.setSetupComplete(true);
        compReadyToOpen = repository.save(compReadyToOpen);
        compReadyToOpen.setMilestones(replaceOpenDateMilestoneDate(compReadyToOpen.getMilestones(), now().plusHours
                (12L)));
        compReadyToOpen = repository.save(compReadyToOpen);
        competitionParticipant = buildCompetitionParticipant(compReadyToOpen, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition compInInform = new Competition(null, null, "compInInform", null, null, null,
                termsAndConditions);
        compInInform.setLeadTechnologist(leadTechnologist);
        compInInform.setSetupComplete(true);
        compInInform = repository.save(compInInform);
        compInInform.setMilestones(replaceOpenDateMilestoneDate(compInInform.getMilestones(), now().minusDays(1L)
                .minusHours(12L)));
        compInInform = repository.save(compInInform);
        competitionParticipant = buildCompetitionParticipant(compInInform, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Competition compInProjectSetup = new Competition(null, null, "compInProjectSetup", null, null, null,
                termsAndConditions);
        compInProjectSetup.setLeadTechnologist(leadTechnologist);
        compInProjectSetup.setSetupComplete(true);
        compInProjectSetup = repository.save(compInProjectSetup);
        compInProjectSetup.setMilestones(replaceOpenDateMilestoneDate(compInProjectSetup.getMilestones(), now()
                .minusDays(2L)));
        compInProjectSetup = repository.save(compInProjectSetup);
        competitionParticipant = buildCompetitionParticipant(compInProjectSetup, leadTechnologist);
        assessmentParticipantRepository.save(competitionParticipant);

        Milestone feedbackReleasedMilestoneInProjectSetup = newMilestone().withCompetition(compInProjectSetup)
                .withType(MilestoneType.FEEDBACK_RELEASED).withDate(now().minusDays(1L)).build();
        milestoneRepository.save(feedbackReleasedMilestoneInProjectSetup);

        Pageable pageable = new PageRequest(0, 40);

        Page<Competition> searchResults = repository.search("%o%", pageable);
        List<Competition> filteredSearchResults = searchResults.getContent().stream().filter(r ->
                existingSearchResults.stream().noneMatch(er -> er.getId().equals(r.getId()))).collect
                (Collectors.toList());
        assertEquals(7, filteredSearchResults.size());
        assertEquals("earliestOpenComp", filteredSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredSearchResults.get(1).getName());
        assertEquals("compInInform", filteredSearchResults.get(2).getName());
        assertEquals("compInPreparation", filteredSearchResults.get(3).getName());
        assertEquals("compWithNoInnovationLead", filteredSearchResults.get(4).getName());
        assertEquals("openComp", filteredSearchResults.get(5).getName());
        assertEquals("compReadyToOpen", filteredSearchResults.get(6).getName());

        Page<Competition> leadTechnologistSearchResults = repository.searchForInnovationLeadOrStakeholder("%o%",
                leadTechnologist.getId(), pageable);
        List<Competition> filteredLeadTechnologistSearchResults = leadTechnologistSearchResults.getContent().stream()
                .filter(r -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L)
                .collect(Collectors.toList());
        assertEquals(4, filteredLeadTechnologistSearchResults.size());
        assertEquals("earliestOpenComp", filteredLeadTechnologistSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredLeadTechnologistSearchResults.get(1).getName());
        assertEquals("compInInform", filteredLeadTechnologistSearchResults.get(2).getName());
        assertEquals("openComp", filteredLeadTechnologistSearchResults.get(3).getName());

        Page<Competition> supportUserSearchResults = repository.searchForSupportUser("%o%", pageable);
        List<Competition> filteredSupportUserSearchResults = supportUserSearchResults.getContent().stream().filter(r
                -> existingSearchResults.stream().filter(er -> er.getId().equals(r.getId())).count() == 0L).collect
                (Collectors.toList());
        assertEquals(6, filteredSupportUserSearchResults.size());
        assertEquals("earliestOpenComp", filteredSupportUserSearchResults.get(0).getName());
        assertEquals("compInProjectSetup", filteredSupportUserSearchResults.get(1).getName());
        assertEquals("compInInform", filteredSupportUserSearchResults.get(2).getName());
        assertEquals("compWithNoInnovationLead", filteredSupportUserSearchResults.get(3).getName());
        assertEquals("openComp", filteredSupportUserSearchResults.get(4).getName());
        assertEquals("compReadyToOpen", filteredSupportUserSearchResults.get(5).getName());
    }

    private AssessmentParticipant buildCompetitionParticipant(Competition competition, User user) {
        AssessmentParticipant competitionParticipant = new AssessmentParticipant();
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
        assertEquals(0L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(0L, results.size());
    }

    @Test
    public void oneQueryCreatedByProjectManager() {
        List<Competition> comps = repository.findByName("Comp21002");
        assertTrue(comps.size() > 0);
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
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
    public void oneQueryCreatedByProjectManagerAndResolved() {
        List<Competition> comps = repository.findByName("Comp21002");
        assertTrue(comps.size() > 0);
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, results.size());
        List<Application> apps = applicationRepository.findByName("App21002");
        assertEquals(1L, apps.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();
        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);

        // get all of the open queries and close them
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        openQueries.forEach(query -> {
            query.closeThread(userRepository.findByEmail("steve.smith@empire.com").get());
            queryRepository.save(query);
        });

        // clean the cache and get some fresh results
        flushAndClearSession();

        assertEquals(0L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(0L, newResults.size());
    }

    @Test
    public void oneQueryCreatedByProjectFinanceWithResponseFromProjectManager() {
        List<Competition> comps = repository.findByName("Comp21003");
        assertTrue(comps.size() > 0);
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
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
    @Rollback
    public void twoQueriesCreatedBySamePartnerSameProject() {

        List<Competition> comps = createTwoQueriesFromSamePartnerSameProject();

        // and see that we now have a single query count because the 2 queries are from the same partner
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, newResults.size());
    }

    @Test
    @Rollback
    public void twoQueriesCreatedBySamePartnerSameProjectAndOneIsResolved() {

        List<Competition> comps = createTwoQueriesFromSamePartnerSameProject();

        // and see that we now have a single query count because the 2 queries are from the same partner
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, results.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();

        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);

        // get all of the open queries and close them
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        Query query = openQueries.get(0);
        query.closeThread(userRepository.findByEmail("steve.smith@empire.com").get());
        queryRepository.save(query);

        // clean the cache and get some fresh results
        flushAndClearSession();

        // assert that we still see a count of one because not all of these partner org's queries are yet resolved
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, newResults.size());
    }

    @Test
    @Rollback
    public void twoQueriesCreatedBySamePartnerSameProjectAndBothAreResolved() {

        List<Competition> comps = createTwoQueriesFromSamePartnerSameProject();

        // and see that we now have a single query count because the 2 queries are from the same partner
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, results.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();

        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);

        // get all of the open queries and close them
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        openQueries.forEach(query -> {
            query.closeThread(userRepository.findByEmail("steve.smith@empire.com").get());
            queryRepository.save(query);
        });

        // clean the cache and get some fresh results
        flushAndClearSession();

        // and see that we now see all resolved
        assertEquals(0L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(0L, newResults.size());
    }

    @Test
    public void twoOpenQueryResponsesFromDifferentPartners() {
        List<Competition> comps = repository.findByName("Comp21005");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
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
    public void twoOpenQueryResponsesFromDifferentPartnersAndOneIsResolved() {
        List<Competition> comps = repository.findByName("Comp21005");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(2L, results.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();
        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);

        // get all of the open queries and close them
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        openQueries.forEach(query -> {
            query.closeThread(userRepository.findByEmail("steve.smith@empire.com").get());
            queryRepository.save(query);
        });

        // clean the cache and get some fresh results
        flushAndClearSession();

        // and see that we now see one resolved
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, newResults.size());
    }

    @Test
    public void twoProjectsHaveOpenQueries() {
        List<Competition> comps = repository.findByName("Comp21006");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
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
    public void twoProjectsHaveOpenQueriesAndOneIsResolved() {
        List<Competition> comps = repository.findByName("Comp21006");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(2L, results.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();
        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);

        // get all of the open queries and close them
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        openQueries.forEach(query -> {
            query.closeThread(userRepository.findByEmail("steve.smith@empire.com").get());
            queryRepository.save(query);
        });

        // clean the cache and get some fresh results
        flushAndClearSession();

        // and see that we now see one resolved
        assertEquals(1L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> newResults = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(1L, newResults.size());
    }

    @Test
    public void oneQueryCreatedByProjectFinanceWithResponseFromProjectManagerButWithSpendProfileGenerated() {
        List<Competition> comps = repository.findByName("Comp21007");
        assertTrue(comps.size() > 0);
        assertEquals(0L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(0L, results.size());
    }

    @Test
    public void countLiveForInnovationLead() {
        // TODO: Improve once IFS-2222 is done.
        long innovationLead1Id = 51L;
        long innovationLead2Id = 52L;

        Long count = repository.countLiveForInnovationLeadOrStakeholder(innovationLead2Id);
        assertEquals(new Long(1L), count);

        count = repository.countLiveForInnovationLeadOrStakeholder(innovationLead1Id);
        assertEquals(new Long(0L), count);
    }

    @Test
    public void findByApplicationsId() {
        loginCompAdmin();

        Competition competition = repository.save(newCompetition()
                .with(id(null))
                .build());

        Application application = applicationRepository.save(newApplication().withId(11L).withCompetition
                (competition).build());

        Competition retrieved = repository.findById(application.getCompetition().getId()).get();

        assertEquals(competition, retrieved);
    }

    // IFS-2263 -- ensure milestone dates aren't rounded up
    @Test
    @Rollback
    public void milestoneDatesTruncated() {
        final ZonedDateTime dateTime = ZonedDateTime.parse("2017-12-03T10:18:30.500Z");
        final ZonedDateTime expectedDateTime = ZonedDateTime.parse("2017-12-03T10:18:30.000Z");

        GrantTermsAndConditions termsAndConditions = new GrantTermsAndConditions();
        termsAndConditions.setId(1L);

        Competition competition = new Competition(null, null, "comp", dateTime, null, null, termsAndConditions);

        Competition savedCompetition = repository.save(competition);

        flushAndClearSession();

        Competition retrievedCompetition = repository.findById(savedCompetition.getId()).get();
        assertTrue(expectedDateTime.isEqual(retrievedCompetition.getStartDate()));
    }

    private List<Competition> createTwoQueriesFromSamePartnerSameProject() {
        // firstly assert that we have 2 unique queries for this competition as 2 partners have open queries currently
        List<Competition> comps = repository.findByName("Comp21005");
        assertTrue(comps.size() > 0);
        assertEquals(2L, repository.countOpenQueriesByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN)).longValue());
        List<CompetitionOpenQueryResource> results = repository.getOpenQueryByCompetitionAndProjectStateNotIn(comps.get(0).getId(), singleton(ProjectState.WITHDRAWN));
        assertEquals(2L, results.size());

        Long projectId = results.get(0).getProjectId();
        Long organisationId = results.get(0).getOrganisationId();

        Project project = projectRepository.findById(projectId).get();
        PartnerOrganisation otherPartnerOrganisation = simpleFindFirst(project.getPartnerOrganisations(), org -> !org
                .getOrganisation().getId().equals(organisationId)).get();

        ProjectFinance projectFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                organisationId);
        ProjectFinance otherPartnerFinanceRow = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
                otherPartnerOrganisation.getOrganisation().getId());

        // now assign one of the queries to the other partner so that they are both coming from the same partner
        List<Query> openQueries = queryRepository.findAllByClassPkAndClassName(projectFinanceRow.getId(),
                ProjectFinance.class.getName());
        Query queryToAssignToOtherPartner = openQueries.get(0);
        ReflectionTestUtils.setField(queryToAssignToOtherPartner, "classPk", otherPartnerFinanceRow.getId());
        queryRepository.save(queryToAssignToOtherPartner);

        // clean the cache and get some fresh results
        flushAndClearSession();
        return comps;
    }
}
