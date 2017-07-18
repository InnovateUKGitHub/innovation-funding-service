package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.NOT_ACCEPTED_OR_SUBMITTED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.REJECTED_AND_SUBMITTED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.application.transactional.AssessorCountSummaryServiceImpl.SUBMITTED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.innovateuk.ifs.workflow.resource.State.ACCEPTED;
import static org.innovateuk.ifs.workflow.resource.State.PENDING;
import static org.innovateuk.ifs.workflow.resource.State.SUBMITTED;
import static org.junit.Assert.assertEquals;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ApplicationStatisticsRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationStatisticsRepository> {

    private static final Collection<State> SUBMITTED_STATUSES = simpleMap(asList(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED), ApplicationState::getBackingState);

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Override
    protected void setRepository(ApplicationStatisticsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetition() throws Exception {
        Long competitionId = 1L;

        List<ApplicationStatistics> statisticsList = repository.findByCompetitionAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES);
        assertEquals(5, statisticsList.size());
    }

    @Test
    public void findByCompetitionPaged() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(1, 3);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES, "", pageable);
        assertEquals(5, statisticsPage.getTotalElements());
        assertEquals(3, statisticsPage.getSize());
        assertEquals(1, statisticsPage.getNumber());
    }

    @Test
    public void findByCompetitionFiltered() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES,"4", pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }

    @Test
    public void findByCompetitionAndInnovationArea() throws Exception {
        long competitionId = 1L;
        long innovationAreaId = 2L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, new String[]{"id"}));
        InnovationArea innovationArea = newInnovationArea()
                .withId(innovationAreaId)
                .withName("Exotic Propulsion")
                .build();
        innovationAreaRepository.save(innovationArea);

        Application application = newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withName("Warp Drive")
                .withNoInnovationAreaApplicable(false)
                .withInnovationArea(innovationArea).build();
        application.getApplicationProcess().setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED));

        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(userMapper.mapToDomain(getPaulPlum()))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withActivityState(assessmentState(PENDING))
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndInnovationAreaProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES, innovationAreaId, pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }

    @Test
    public void getAssessorCountSummaryByCompetition() throws Exception {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.save(users);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        competitionParticipantRepository.save(competitionParticipants);

        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(users.get(0))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withActivityState(assessmentState(PENDING))
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage = repository.getAssessorCountSummaryByCompetition(competitionId, pageable);

        assertEquals(2, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(2, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(0).getId(), users.get(0).getName(), profiles.get(0).getSkillsAreas(), 1L,1L, 0L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_accepted() throws Exception {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.save(users);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        competitionParticipantRepository.save(competitionParticipants);

        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(users.get(0))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withActivityState(assessmentState(ACCEPTED))
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage = repository.getAssessorCountSummaryByCompetition(competitionId, pageable);

        assertEquals(2, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(2, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(0).getId(), users.get(0).getName(), profiles.get(0).getSkillsAreas(), 1L,1L, 1L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_otherCompetition() throws Exception {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);
        final Competition otherCompetition = newCompetition().with(id(null)).build();
        competitionRepository.save(otherCompetition);

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.save(users);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        competitionParticipantRepository.save(competitionParticipants);

        List<CompetitionParticipant> otherCompetitionParticipants = newCompetitionParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(otherCompetition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        competitionParticipantRepository.save(otherCompetitionParticipants);


        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(users.get(0))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withActivityState(assessmentState(ACCEPTED))
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(otherCompetition.getId(), pageable);

        assertEquals(2, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(2, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(0).getId(), users.get(0).getName(), profiles.get(0).getSkillsAreas(), 1L,0L, 0L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_submitted() throws Exception {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.save(users);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        competitionParticipantRepository.save(competitionParticipants);

        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(users.get(0))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withActivityState(assessmentState(SUBMITTED))
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, pageable);

        assertEquals(2, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(2, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessorCountSummaryResource = newAssessorCountSummaryResource()
                .withId(users.get(0).getId())
                .withName(users.get(0).getName())
                .withSkillAreas(profiles.get(0).getSkillsAreas())
                .withTotalAssigned(0L)
                .withAssigned(0L)
                .withAccepted(0L)
                .withSubmitted(1L)
                .build();

        assertEquals(expectedAssessorCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_noAssessorsOnCompetition() throws Exception {
        final long competitionId = 1L;

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, pageable);

        assertEquals(0, statisticsPage.getTotalElements());
        assertEquals(0, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(0, statisticsPage.getNumberOfElements());
    }

    private ActivityState assessmentState(State state) {
        return activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, state);
    }
}
