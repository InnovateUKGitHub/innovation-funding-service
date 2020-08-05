package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus.*;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

@Rollback
public class ApplicationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private InnovationLeadRepository innovationLeadRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByApplicationProcessActivityStateStateIn() {
        List<Application> applicationList = simpleMap(ApplicationState.values(), this::createApplicationByState);

        long initial = repository.findByApplicationProcessActivityStateIn(submittedAndFinishedStates).count();

        repository.saveAll(applicationList);

        Stream<Application> applications = repository.findByApplicationProcessActivityStateIn(submittedAndFinishedStates);

        assertEquals(initial + 5, applications.count());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withActivityState(CREATED, SUBMITTED)
                .with(id(null))
                .build(2);

        applicationRepository.saveAll(applications);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition
                .getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_noApplications() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        Pageable pageable = PageRequest.of(0, 20);

        Page<Application> applications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition.getId(),
                pageable);

        assertEquals(0, applications.getTotalElements());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_staged() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withActivityState(SUBMITTED)
                .with(id(null))
                .build(2);

        applicationRepository.saveAll(applications);

        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(InterviewAssignmentState.CREATED)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        Pageable pageable = PageRequest.of(1, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition
                .getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    @Test
    public void findSubmittedApplicationsNotOnInterviewPanel_inviteSent() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withActivityState(SUBMITTED)
                .build(2);

        applicationRepository.saveAll(applications);

        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .with(id(null))
                .withState(InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewAssignment);

        Pageable pageable = PageRequest.of(1, 20);

        Page<Application> invitableApplications = repository.findSubmittedApplicationsNotOnInterviewPanel(competition
                .getId(), pageable);

        assertEquals(1, invitableApplications.getTotalElements());
    }

    @Test
    public void findByProjectId() {
        Application application = applicationRepository.save(newApplication().withId(17L).build());
        Project project = projectRepository.save(newProject()
                .withApplication(application)
                .withId(17L)
                .withName("Project Name")
                .build()
        );

        Application retrieved = repository.findByProjectId(project.getId());

        assertEquals(application, retrieved);
    }

    @Test
    public void findByAssessmentId() {
        Application application = repository.save(newApplication().build());

        Assessment assessment = assessmentRepository.save(newAssessment()
                .with(id(null))
                .withApplication(application)
                .withProcessState(AssessmentState.SUBMITTED)
                .build()
        );

        Application retrieved = repository.findByAssessmentId(assessment.getId());

        assertEquals(application, retrieved);
    }

    @Test
    public void findTopByCompetitionIdOrderByManageFundingEmailDateDesc() {
        loginCompAdmin();

        List<Competition> competitions = stream(
                competitionRepository.saveAll(newCompetition()
                        .with(id(null))
                        .build(2)).spliterator(), false).collect(toList());

        ZonedDateTime[] zonedDateTimes = IntStream.rangeClosed(1, 6).mapToObj(i ->
                ZonedDateTime.of(2018, 8, 1, i, 0, 0, 0, ZoneId.systemDefault()))
                .toArray(ZonedDateTime[]::new);

        Competition competition1 = competitions.get(0);
        Competition competition2 = competitions.get(1);

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition1, competition1, competition1, competition2, competition2, competition2)
                .withManageFundingEmailDate(zonedDateTimes)
                .build(6);

        List<Application> saved = stream(repository.saveAll(applications).spliterator(), false).collect(toList());

        Application expectedApplicationComp2WithMaxDate = saved.get(5);

        assertEquals(expectedApplicationComp2WithMaxDate, repository
                .findTopByCompetitionIdOrderByManageFundingEmailDateDesc(competition2.getId()).get());
    }

    private Application createApplicationByState(ApplicationState applicationState) {
        Application application = newApplication()
                .with(id(null))
                .withApplicationState(applicationState)
                .build();
        application.getApplicationProcess().setProcessState(applicationState);
        return application;
    }

    @Test
    public void searchApplicationsByUserIdAndInnovationLeadRole() {
        loginCompAdmin();

        User user = new User("Innovation", "Lead", "innovationLead@gmail.com", "", "123IL");

        List<Competition> competitions = newCompetition().with(id(null)).withLeadTechnologist().build(2);

        List<Application> applications = newApplication()
                .withCompetition(competitions.get(0), competitions.get(1))
                .with(id(null))
                .withName("app1", "app2")
                .build(2);

        InnovationLead innovationLead = new InnovationLead(competitions.get(0), user);

        Pageable pageable = PageRequest.of(1, 40);

        userRepository.save(user);
        competitionRepository.saveAll(competitions);
        applicationRepository.saveAll(applications);
        innovationLeadRepository.save(innovationLead);

        Page<Application> foundApplication = repository.searchApplicationsByUserIdAndInnovationLeadRole(user.getId(), applications.get(0).getId().toString(), pageable);
        Page<Application> notFoundApplication = repository.searchApplicationsByUserIdAndInnovationLeadRole(user.getId(), applications.get(1).getId().toString(), pageable);

        assertEquals(1, foundApplication.getTotalElements());
        assertEquals(0, notFoundApplication.getTotalElements());
    }

    @Test
    public void searchApplicationsByUserIdAndStakeholderRole() {

        loginCompAdmin();

        User user = new User("Stake", "Holder", "stakeholder@gmail.com", "", "123abc");

        List<Competition> competitions = newCompetition().with(id(null)).withLeadTechnologist().build(2);

        List<Application> applications = newApplication()
                .withCompetition(competitions.get(0), competitions.get(1))
                .with(id(null))
                .withName("app1", "app2")
                .build(2);

        Stakeholder stakeholder = new Stakeholder(competitions.get(0), user);

        Pageable pageable = PageRequest.of(1, 40);

        userRepository.save(user);
        competitionRepository.saveAll(competitions);
        stakeholderRepository.save(stakeholder);
        applicationRepository.saveAll(applications);

        Page<Application> foundApplication = repository.searchApplicationsByUserIdAndStakeholderRole(user.getId(), applications.get(0).getId().toString(), pageable);
        Page<Application> notFoundApplication = repository.searchApplicationsByUserIdAndStakeholderRole(user.getId(), applications.get(1).getId().toString(), pageable);

        assertEquals(1, foundApplication.getTotalElements());
        assertEquals(0, notFoundApplication.getTotalElements());
    }

    @Test
    public void previous() {
        loginCompAdmin();

        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        //Previous
        Application withoutProject = applicationRepository.save(newApplication()
                .withCompetition(competition)
                .with(id(null))
                .withName("applicationWithoutProject")
                .withActivityState(APPROVED)
                .build());

        Organisation lead = newOrganisation()
                .with(id(null))
                .withName("Lead")
                .build();
        organisationRepository.save(lead);

        ProcessRole pr = newProcessRole()
                .with(id(null))
                .withUser(userRepository.findById(getSteveSmith().getId()).get())
                .withOrganisationId(lead.getId())
                .withRole(Role.LEADAPPLICANT)
                .withApplication(withoutProject)
                .build();

        // Not previous
        Application withProject = newApplication()
                .withCompetition(competition)
                .with(id(null))
                .withName("applicationWithProject")
                .withActivityState(APPROVED)
                .build();
        Project project = newProject()
                .withApplication(withProject)
                .withName(withProject.getName())
                .build();

        Application unsubmitted = newApplication()
                .withCompetition(competition)
                .with(id(null))
                .withName("unsubmitted")
                .withActivityState(OPENED)
                .build();

        applicationRepository.saveAll(asList(withProject, unsubmitted));
        projectRepository.save(project);
        processRoleRepository.save(pr);

        assertEquals(1, repository.countPrevious(competition.getId()));

        List<PreviousApplicationResource> previous = repository.findPrevious(competition.getId());

        assertEquals(1, previous.size());
        assertEquals(APPROVED, previous.get(0).getApplicationState());
        assertEquals("applicationWithoutProject", previous.get(0).getName());
        assertEquals((long) competition.getId(), previous.get(0).getCompetition());
        assertEquals((long) withoutProject.getId(), previous.get(0).getId());
        assertEquals("Lead", previous.get(0).getLeadOrganisationName());

    }

    @Test
    public void findApplicationsForDashboard() {
        loginCompAdmin();

        Competition competition = newCompetition().with(id(null)).build();
        User user = new User("Person", "Applicant", "person@gmail.com", "", "123abc");

        Application leadApp = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("leadApp")
                .build();

        Application collabApp = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("collabApp")
                .build();

        Application assessorApp = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("assessorApps")
                .build();

        Application userOnProjectButNotApplication = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("userOnProjectButNotApplication")
                .build();

        Application userOnAppButNotProject = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("userOnAppButNotProject")
                .build();

        Project projectWithoutUser = newProject()
                .with(id(null))
                .withName("projectWithoutUser")
                .withApplication(userOnAppButNotProject)
                .withProjectUsers(newProjectUser()
                        .with(id(null))
                        .withUser(userRepository.findById(getSteveSmith().getId()).get())
                        .withRole(ProjectParticipantRole.PROJECT_PARTNER)
                        .build(1))
                .build();

        Project projectWithUser = newProject()
                .with(id(null))
                .withName("projectWithUser")
                .withApplication(userOnProjectButNotApplication)
                .withProjectUsers(newProjectUser()
                        .with(id(null))
                        .withUser(user)
                        .withRole(ProjectParticipantRole.PROJECT_PARTNER)
                        .build(1))
                .build();

        userRepository.save(user);
        competitionRepository.save(competition);
        applicationRepository.saveAll(asList(leadApp, collabApp, assessorApp, userOnProjectButNotApplication, userOnAppButNotProject));
        projectRepository.saveAll(asList(projectWithoutUser, projectWithUser));


        ProcessRole leadRole = newProcessRole()
                .with(id(null))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(leadApp)
                .withUser(user)
                .build();

        ProcessRole collaboratorRole = newProcessRole()
                .with(id(null))
                .withRole(Role.COLLABORATOR)
                .withApplication(collabApp)
                .withUser(user)
                .build();

        ProcessRole assessorRole = newProcessRole()
                .with(id(null))
                .withRole(Role.ASSESSOR)
                .withApplication(assessorApp)
                .withUser(user)
                .build();

        ProcessRole appRoleButNotOnProject = newProcessRole()
                .with(id(null))
                .withRole(Role.COLLABORATOR)
                .withApplication(userOnAppButNotProject)
                .withUser(user)
                .build();

        processRoleRepository.saveAll(asList(leadRole, collaboratorRole, assessorRole, appRoleButNotOnProject));

        List<Application> applications = repository.findApplicationsForDashboard(user.getId());

        assertEquals(3, applications.size());
        assertTrue(applications.stream().anyMatch(app -> app.getId().equals(leadApp.getId())));
        assertTrue(applications.stream().anyMatch(app -> app.getId().equals(collabApp.getId())));
        assertTrue(applications.stream().anyMatch(app -> app.getId().equals(userOnProjectButNotApplication.getId())));

        assertFalse(applications.stream().anyMatch(app -> app.getId().equals(userOnAppButNotProject.getId())));
        assertFalse(applications.stream().anyMatch(app -> app.getId().equals(assessorApp.getId())));
    }

    @Test
    public void findByApplicationStateAndFundingDecision() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withActivityState(SUBMITTED)
                .withFundingDecision(FUNDED, null, UNFUNDED)
                .build(3);

        applicationRepository.saveAll(applications);

        List<Application> foundApplications = repository.findByApplicationStateAndFundingDecision(competition.getId(), SUBMITTED_STATES, null, FUNDED, false);

        assertEquals(1, foundApplications.size());
    }

    @Test
    public void findByApplicationStateAndFundingDecision_undecided() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withActivityState(SUBMITTED)
                .withFundingDecision(UNDECIDED, null)
                .build(2);

        applicationRepository.saveAll(applications);

        List<Application> foundApplications = repository.findByApplicationStateAndFundingDecision(competition.getId(), SUBMITTED_STATES, null, UNDECIDED, false);

        assertEquals(2, foundApplications.size());
    }

    @Test
    public void findByApplicationStateAndFundingDecision_funded() {
        loginCompAdmin();
        Competition competition = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withFundingDecision(FUNDED, null)
                .withActivityState(SUBMITTED, APPROVED)
                .build(2);

        applicationRepository.saveAll(applications);

        flushAndClearSession();

        List<Application> foundApplications = repository.findByApplicationStateAndFundingDecision(competition.getId(), SUBMITTED_STATES, null, FUNDED, false);

        assertEquals(2, foundApplications.size());
    }
}