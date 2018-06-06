package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository.SORT_BY_FIRSTNAME;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ApplicationStatisticsRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationStatisticsRepository> {

    private static final Collection<ApplicationState> SUBMITTED_STATUSES = asList(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED);

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private InnovationSectorRepository innovationSectorRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Override
    protected void setRepository(ApplicationStatisticsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetition() {
        Long competitionId = 1L;

        List<ApplicationStatistics> statisticsList = repository.findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATUSES);
        assertEquals(5, statisticsList.size());
    }

    @Test
    public void findByCompetitionPaged() {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(1, 3);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATUSES, "", pageable);
        assertEquals(5, statisticsPage.getTotalElements());
        assertEquals(3, statisticsPage.getSize());
        assertEquals(1, statisticsPage.getNumber());
    }

    @Test
    public void findByCompetitionFiltered() {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATUSES,"4", pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }


    @Test
    public void findByCompetitionAndInnovationArea() {
        long competitionId = 1L;
        long innovationAreaId = 54L;
        long assessorId = 20L;

        Application application = newApplication()
                .with(id(null))
                .withApplicationState(ApplicationState.SUBMITTED)
                .withName("Warp Drive")
                .withNoInnovationAreaApplicable(false)
                .withCompetition(competitionRepository.findById(competitionId))
                .withInnovationArea(innovationAreaRepository.findOne(innovationAreaId))
                .build();
        application.getApplicationProcess().setProcessState(ApplicationState.SUBMITTED);

        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(APPLICANT)
                .withUser(userMapper.mapToDomain(getSteveSmith()))
                .withApplication(application)
                .build();

        processRoleRepository.save(processRole);



        flushAndClearSession();

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "id"));

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndInnovationAreaProcessActivityStateIn(competitionId, assessorId, SUBMITTED_STATUSES, "", innovationAreaId, pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }

    @Test
    public void findByCompetitionAndInnovationAreaFiltered() {
        long competitionId = 1L;
        long innovationAreaId = 12L;
        long assessorId = 20L;

        Application application = newApplication()
                .with(id(null))
                .withApplicationState(ApplicationState.SUBMITTED)
                .withName("Nuclear pulse propulsion")
                .withNoInnovationAreaApplicable(false)
                .withCompetition(competitionRepository.findById(competitionId))
                .withInnovationArea(innovationAreaRepository.findOne(innovationAreaId))
                .build();
        application.getApplicationProcess().setProcessState(ApplicationState.SUBMITTED);

        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(APPLICANT)
                .withUser(userMapper.mapToDomain(getSteveSmith()))
                .withApplication(application)
                .build();

        processRoleRepository.save(processRole);

        flushAndClearSession();

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "id"));

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetitionAndInnovationAreaProcessActivityStateIn(competitionId, assessorId, SUBMITTED_STATUSES, "", innovationAreaId, pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }

    @Test
    public void getAssessorCountSummaryByCompetition() {
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

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

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
                .withProcessState(AssessmentState.PENDING)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.empty(), pageable);

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
    public void getAssessorCountSummaryByCompetition_sortByFirstName() {
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

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

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
                .withProcessState(AssessmentState.PENDING)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize, SORT_BY_FIRSTNAME);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.empty(), pageable);

        assertEquals(2, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(2, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(1).getId(), users.get(1).getName(), profiles.get(1).getSkillsAreas(), 0L,0L, 0L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_innovationSector() {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);

        InnovationSector innovationSector = newInnovationSector()
                .with(id(null))
                .withName("Artificial intelligence")
                .build();
        innovationSectorRepository.save(innovationSector);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .with(id(null))
                .withName("Machine learning")
                .withSector(innovationSector)
                .build(2);
        innovationAreaRepository.save(innovationAreas);

        assertNotNull(innovationSector.getId());

        List<Profile> profiles = newProfile()
                .with(id(null))
                .withSkillsAreas("Java Development")
                .withInnovationAreas(innovationAreas, emptyList())
                .build(2);

        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.save(users);

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

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
                .withProcessState(AssessmentState.PENDING)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.ofNullable(innovationSector.getId()), Optional.empty(), pageable);

        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(1, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(0).getId(), users.get(0).getName(), profiles.get(0).getSkillsAreas(), 1L,1L, 0L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_businessType() {
        long competitionId = 1L;

        loginCompAdmin();
        Competition competition = competitionRepository.findById(competitionId);

        List<Profile> profiles = newProfile()
                .with(id(null))
                .withSkillsAreas("Java Development")
                .withBusinessType(BusinessType.BUSINESS, BusinessType.ACADEMIC).build(2);
        profileRepository.save(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8", "f6b9ddeb-f169-4ac4-b606-90cb877ce8c9")
                .build(2);
        userRepository.save(users);

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(ASSESSOR)
                .withApplication(application)
                .withUser(users.get(1))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withProcessState(AssessmentState.PENDING)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.of(BusinessType.ACADEMIC), pageable);

        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(1, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(1, statisticsPage.getNumberOfElements());

        final AssessorCountSummaryResource assessorCountSummaryResource = statisticsPage.getContent().get(0);

        final AssessorCountSummaryResource expectedAssessmentCountSummaryResource = new AssessorCountSummaryResource(
                users.get(1).getId(), users.get(1).getName(), profiles.get(1).getSkillsAreas(), 1L,1L, 0L, 0L);

        assertEquals(expectedAssessmentCountSummaryResource, assessorCountSummaryResource);
    }

    @Test
    public void getAssessorCountSummaryByCompetition_accepted() {
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

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

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
                .withProcessState(AssessmentState.ACCEPTED)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage = repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.empty(), pageable);

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
    public void getAssessorCountSummaryByCompetition_otherCompetition() {
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

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

        List<AssessmentParticipant> otherCompetitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(otherCompetition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(otherCompetitionParticipants);


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
                .withProcessState(AssessmentState.ACCEPTED)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(otherCompetition.getId(), Optional.empty(), Optional.empty(),  pageable);

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
    public void getAssessorCountSummaryByCompetition_submitted() {
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

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.save(competitionParticipants);

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
                .withProcessState(AssessmentState.SUBMITTED)
                .build();

        assessmentRepository.save(assessment);

        flushAndClearSession();

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.empty(), pageable);

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
    public void getAssessorCountSummaryByCompetition_noAssessorsOnCompetition() {
        final long competitionId = 1L;

        final int pageSize = 10;
        final int pageNumber = 0;
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<AssessorCountSummaryResource> statisticsPage =
                repository.getAssessorCountSummaryByCompetition(competitionId, Optional.empty(), Optional.empty(), pageable);

        assertEquals(0, statisticsPage.getTotalElements());
        assertEquals(0, statisticsPage.getTotalPages());
        assertEquals(pageSize, statisticsPage.getSize());
        assertEquals(pageNumber, statisticsPage.getNumber());
        assertEquals(0, statisticsPage.getNumberOfElements());
    }
}