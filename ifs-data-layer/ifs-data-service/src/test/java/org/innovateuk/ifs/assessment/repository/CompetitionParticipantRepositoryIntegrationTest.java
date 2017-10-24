package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.repository.AssessmentPanelInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteBuilder.newCompetitionInviteWithoutId;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElement;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class CompetitionParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionParticipantRepository> {

    private Competition competition;
    private InnovationArea innovationArea;
    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private AssessmentPanelInviteRepository assessmentPanelInviteRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionParticipantRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        competition = competitionRepository.save(newCompetition().withName("competition").build());
        innovationArea = innovationAreaRepository.save(newInnovationArea().withName("innovation area").build());
        user = userRepository.findByEmail("paul.plum@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Expected to find test user for email paul.plum@gmail.com"));
    }

    @Test
    public void findAll() {
        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(2)
        );
        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipant = repository.findAll();

        assertEquals(2, retrievedParticipant.size());
        assertEqualParticipants(savedParticipants, retrievedParticipant);
    }

    @Test
    public void getByInviteHash() {
        String hash = generateInviteHash();

        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(hash)
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build()
        );
        flushAndClearSession();

        CompetitionAssessmentParticipant retrievedParticipant = repository.getByInviteHash(hash);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save() {
        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build()
        );
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionAssessmentParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_accepted() {
        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        savedParticipant.acceptAndAssignUser(user);
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionAssessmentParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_rejected() {
        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .build()
        );

        RejectionReason reason = rejectionReasonRepository.findAll().get(0);
        savedParticipant.reject(reason, Optional.of("too busy"));
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionAssessmentParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getByUserRoleStatus() {
        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.getByUserIdAndRole(user.getId(), ASSESSOR);
        assertEqualParticipants(savedParticipant, getOnlyElement(retrievedParticipants));
    }

    @Test
    public void getByCompetitionAndRole() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(1))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(2));

        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.getByCompetitionIdAndRole(competitions.get(0).getId(), ASSESSOR);

        assertNotNull(retrievedParticipants);
        assertEquals(1, retrievedParticipants.size());
        assertEqualParticipants(savedParticipants.get(0), retrievedParticipants.get(0));
    }

    @Test
    public void getByCompetitionIdAndRoleAndStatus() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2", "name3")
                        .withEmail("test1@test.com", "test2@test.com", "test3@test.com")
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(0), competitions.get(1))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3));

        // Now accept one of the invites
        CompetitionAssessmentParticipant competitionParticipantToAccept = savedParticipants.get(1);
        competitionParticipantToAccept.getInvite().open();
        competitionParticipantToAccept.acceptAndAssignUser(user);

        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.getByCompetitionIdAndRoleAndStatus(1L, ASSESSOR, ParticipantStatus.ACCEPTED);

        assertNotNull(retrievedParticipants);
        assertEqualParticipants(singletonList(competitionParticipantToAccept), retrievedParticipants);
    }

    @Test
    public void findParticipantsWithAssessments() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        Application application = applicationRepository.findByCompetitionId(competitions.get(0).getId()).get(0);

        Role role = roleRepository.findOneByName(UserRoleType.ASSESSOR.getName());

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION_ASSESSMENT, State.ACCEPTED);
        List<User> users = findUsersByEmail("paul.plum@gmail.com", "felix.wilson@gmail.com", "steve.smith@empire.com");
        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2", "name3")
                        .withEmail("test1@test.com", "test2@test.com", "test3@test.com")
                        .withUser(users.toArray(new User[users.size()]))
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(0), competitions.get(1))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3));

        // Now accept all of the invites
        savedParticipants.forEach(
                participant -> {
                    participant.getInvite().open();
                    participant.acceptAndAssignUser(participant.getInvite().getUser());
                }
        );

        // Now assign two of the participants
        for (int i = 0; i < 2; i++) {

            ProcessRole processRole = new ProcessRole();
            processRole.setUser(users.get(i));
            processRole.setApplicationId(application.getId());
            processRole.setRole(role);
            processRoleRepository.save(processRole);

            Assessment assessment = new Assessment(application, processRole);
            assessment.setActivityState(activityState);
            assessmentRepository.save(assessment);
        }

        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.findParticipantsWithAssessments(1L, ASSESSOR, ParticipantStatus.ACCEPTED, 1L);

        assertNotNull(retrievedParticipants);
        assertEquals(2, retrievedParticipants.size());
    }

    @Test
    public void findParticipantsWithoutAssessments() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        Application application = applicationRepository.findByCompetitionId(competitions.get(0).getId()).get(0);

        Role role = roleRepository.findOneByName(UserRoleType.ASSESSOR.getName());

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION_ASSESSMENT, State.ACCEPTED);
        List<User> users = findUsersByEmail("paul.plum@gmail.com", "felix.wilson@gmail.com", "steve.smith@empire.com");
        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2", "name3")
                        .withEmail("test1@test.com", "test2@test.com", "test3@test.com")
                        .withUser(users.toArray(new User[users.size()]))
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(0), competitions.get(0))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3));

        // Now accept all of the invites
        savedParticipants.forEach(
                participant -> {
                    participant.getInvite().open();
                    participant.acceptAndAssignUser(participant.getInvite().getUser());
                }
        );

        // Now assign one of the participants
        ProcessRole processRole = new ProcessRole();
        processRole.setUser(users.get(0));
        processRole.setApplicationId(application.getId());
        processRole.setRole(role);
        processRoleRepository.save(processRole);

        Assessment assessment = new Assessment(application, processRole);
        assessment.setActivityState(activityState);
        assessmentRepository.save(assessment);

        flushAndClearSession();

        Pageable pagination = new PageRequest(0, 1);

        Page<CompetitionAssessmentParticipant> retrievedParticipants = repository.findParticipantsWithoutAssessments(1L, ASSESSOR, ParticipantStatus.ACCEPTED, 1L, null, pagination);

        assertNotNull(retrievedParticipants);
        assertEquals(2, retrievedParticipants.getTotalElements());
    }

    private List<User> findUsersByEmail(String... emails) {
        return Arrays.stream(emails).map(email -> userRepository.findByEmail(email).get()).collect(toList());
    }

    @Test
    public void countByCompetitionIdAndRoleAndStatus() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2", "name3")
                        .withEmail("test1@test.com", "test2@test.com", "test3@test.com")
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(0), competitions.get(1))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3));

        // Now accept one of the invites
        for (int i = 0; i < 1; i++) {
            CompetitionAssessmentParticipant competitionParticipantToAccept = savedParticipants.get(i);
            competitionParticipantToAccept.getInvite().open();
            competitionParticipantToAccept.acceptAndAssignUser(user);
        }

        flushAndClearSession();

        long count = repository.countByCompetitionIdAndRoleAndStatus(1L, ASSESSOR, ParticipantStatus.ACCEPTED);

        assertEquals(1L, count);
    }

    @Test
    public void getByInviteEmail() {
        List<Competition> competitions = newCompetition()
                .with(id(null))
                .build(2);
        competitionRepository.save(competitions);

        List<CompetitionAssessmentParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name1", "name2")
                        .withEmail("test1@test.com", "test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(1), competitions.get(0))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3));

        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.getByInviteEmail("test1@test.com");
        assertEqualParticipants(asList(savedParticipants.get(0), savedParticipants.get(1)), retrievedParticipants);
    }

    @Test
    public void getByInviteId() {
        CompetitionAssessmentParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build());

        flushAndClearSession();

        CompetitionAssessmentParticipant retrievedParticipant = repository.getByInviteId(savedParticipant.getInvite().getId());
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void countByCompetitionIdAndRole() throws Exception {
        saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(newCompetition().withId(1L).build())
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(2)
        );

        flushAndClearSession();

        int participantCount = repository.countByCompetitionIdAndRole(1L, ASSESSOR);

        assertEquals(2, participantCount);
    }

    private CompetitionAssessmentParticipant saveNewCompetitionParticipant(CompetitionInvite invite) {
        return repository.save(new CompetitionAssessmentParticipant(invite));
    }

    private List<CompetitionAssessmentParticipant> saveNewCompetitionParticipants(List<CompetitionInvite> invites) {
        return invites.stream().map(competitionInvite ->
                repository.save(new CompetitionAssessmentParticipant(competitionInvite))).collect(toList());
    }

    private void assertEqualParticipants(List<CompetitionAssessmentParticipant> expected, List<CompetitionAssessmentParticipant> actual) {
        zip(expected, actual, this::assertEqualParticipants);
    }

    private void assertEqualParticipants(CompetitionAssessmentParticipant expected, CompetitionAssessmentParticipant actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getRejectionReasonComment(), actual.getRejectionReasonComment());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getStatus(), actual.getStatus());

        assertTrue((expected.getProcess() == null && actual.getProcess() == null) ||
                (expected.getProcess() != null && actual.getProcess() != null &&
                        expected.getProcess().getId().equals(actual.getProcess().getId()))
        );

        assertTrue((expected.getUser() == null && actual.getUser() == null) ||
                (expected.getUser() != null && actual.getUser() != null &&
                        expected.getUser().getId().equals(actual.getUser().getId())));

        assertTrue((expected.getInvite() == null && actual.getInvite() == null) ||
                (expected.getInvite() != null && actual.getInvite() != null &&
                        expected.getInvite().getId().equals(actual.getInvite().getId())));

        assertTrue((expected.getRejectionReason() == null && actual.getRejectionReason() == null) ||
                (expected.getRejectionReason() != null && actual.getRejectionReason() != null &&
                        expected.getRejectionReason().getId().equals(actual.getRejectionReason().getId())));
    }

    @Test
    public void getAssessorsByCompetitionAndStatus() throws Exception {
        loginSteveSmith();

        User acceptedUser = newUser()
                .withId()
                .withUid("uid-1")
                .withFirstName("Anthony")
                .withLastName("Hale")
                .withProfileId()
                .build();

        userRepository.save(acceptedUser);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withStatus(SENT)
                .build(4);

        List<CompetitionAssessmentParticipant> competitionParticipants = saveNewCompetitionParticipants(newAssessorInvites);

        competitionParticipants.get(3).getInvite().open();
        competitionParticipants.get(3).acceptAndAssignUser(acceptedUser);

        repository.save(competitionParticipants);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndStatusContains(
                competition.getId(),
                singletonList(ACCEPTED),
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Anthony Hale", content.get(0).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_allFilters() throws Exception {
        loginCompAdmin();

        Agreement agreement = agreementRepository.findOne(1L);
        InnovationArea otherInnovationArea = innovationAreaRepository.findOne(5L);

        List<Profile> profiles = newProfile()
                .withId()
                .withAgreement(agreement)
                .withInnovationAreas(singletonList(otherInnovationArea), singletonList(innovationArea), singletonList(otherInnovationArea))
                .withSkillsAreas("Skill area 1", "Skill area 2", "Skill area 3", "Skill area 4")
                .build(4);

        profileRepository.save(profiles);

        List<User> users = newUser()
                .withId()
                .withUid("uid-1", "uid-2", "uid-3", "uid-4")
                .withFirstName("Jane", "Charles", "Claire", "Anthony")
                .withLastName("Pritchard", "Dance", "Jenkins", "Hale")
                .withProfileId(
                        profiles.get(0).getId(),
                        profiles.get(1).getId(),
                        profiles.get(2).getId(),
                        profiles.get(3).getId()
                )
                .build(4);

        userRepository.save(users);

        users.get(0).setAffiliations(
                newAffiliation()
                        .withId()
                        .withAffiliationType(AffiliationType.PROFESSIONAL)
                        .withDescription("Affiliation Description 1")
                        .withExists(TRUE)
                        .withUser(users.get(0))
                        .build(1)
        );
        users.get(1).setAffiliations(
                newAffiliation()
                        .withId()
                        .withAffiliationType(AffiliationType.PROFESSIONAL)
                        .withDescription("Affiliation Description 2")
                        .withExists(TRUE)
                        .withUser(users.get(1))
                        .build(1)
        );
        users.get(3).setAffiliations(
                newAffiliation()
                        .withId()
                        .withAffiliationType(AffiliationType.PROFESSIONAL)
                        .withDescription("Affiliation Description 3")
                        .withExists(TRUE)
                        .withUser(users.get(3))
                        .build(1)
        );

        userRepository.save(users);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(otherInnovationArea, innovationArea, otherInnovationArea)
                .withUser(users.get(0), users.get(1), users.get(2), users.get(3))
                .withStatus(SENT)
                .build(4);

        List<CompetitionAssessmentParticipant> competitionParticipants = saveNewCompetitionParticipants(newAssessorInvites);

        competitionParticipants.get(1).getInvite().open();
        competitionParticipants.get(1).acceptAndAssignUser(users.get(1));

        repository.save(competitionParticipants);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                innovationArea.getId(),
                singletonList(ACCEPTED),
                TRUE,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Charles Dance", content.get(0).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_innovationArea() throws Exception {
        InnovationArea otherInnovationArea = innovationAreaRepository.findOne(5L);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(otherInnovationArea, otherInnovationArea, innovationArea, otherInnovationArea)
                .withStatus(SENT)
                .build(4);

        saveNewCompetitionParticipants(newAssessorInvites);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                innovationArea.getId(),
                asList(ACCEPTED, PENDING),
                null,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Claire Jenkins", content.get(0).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_participantStatus() throws Exception {
        loginSteveSmith();

        User acceptedUser = newUser()
                .withId()
                .withUid("uid-1")
                .withFirstName("Anthony")
                .withLastName("Hale")
                .withProfileId()
                .build();

        userRepository.save(acceptedUser);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withStatus(SENT)
                .build(4);

        List<CompetitionAssessmentParticipant> competitionParticipants = saveNewCompetitionParticipants(newAssessorInvites);

        competitionParticipants.get(3).getInvite().open();
        competitionParticipants.get(3).acceptAndAssignUser(acceptedUser);

        repository.save(competitionParticipants);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                null,
                singletonList(ACCEPTED),
                null,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Anthony Hale", content.get(0).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_isCompliant() throws Exception {
        loginCompAdmin();

        Agreement agreement = agreementRepository.findOne(1L);

        List<Profile> profiles = newProfile()
                .withId()
                .withAgreement(agreement)
                .withInnovationAreas(singletonList(innovationArea))
                .withSkillsAreas("Skill area 1", "Skill area 2", "Skill area 3", "Skill area 4")
                .build(4);

        profileRepository.save(profiles);

        List<User> users = newUser()
                .withId()
                .withUid("uid-1", "uid-2", "uid-3", "uid-4")
                .withFirstName("Jane", "Charles", "Claire", "Anthony")
                .withLastName("Pritchard", "Dance", "Jenkins", "Hale")
                .withProfileId(
                        profiles.get(0).getId(),
                        profiles.get(1).getId(),
                        profiles.get(2).getId(),
                        profiles.get(3).getId()
                )
                .build(4);

        userRepository.save(users);

        users.get(0).setAffiliations(
                newAffiliation()
                        .withId()
                        .withAffiliationType(AffiliationType.PROFESSIONAL)
                        .withDescription("Affiliation Description 1")
                        .withExists(TRUE)
                        .withUser(users.get(0))
                        .build(1)
        );

        userRepository.save(users);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withUser(users.get(0), users.get(1), users.get(2), users.get(3))
                .withStatus(SENT)
                .build(4);

        saveNewCompetitionParticipants(newAssessorInvites);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                null,
                singletonList(PENDING),
                TRUE,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Jane Pritchard", content.get(0).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_isNotCompliant() throws Exception {
        loginCompAdmin();

        Agreement agreement = agreementRepository.findOne(1L);

        List<Profile> profiles = newProfile()
                .withId()
                .withAgreement(agreement)
                .withInnovationAreas(singletonList(innovationArea))
                .withSkillsAreas("Skill area 1", "Skill area 2", "Skill area 3", "Skill area 4")
                .build(4);

        profileRepository.save(profiles);

        List<User> users = newUser()
                .withId()
                .withUid("uid-1", "uid-2", "uid-3", "uid-4")
                .withFirstName("Jane", "Charles", "Claire", "Anthony")
                .withLastName("Pritchard", "Dance", "Jenkins", "Hale")
                .withProfileId(
                        profiles.get(0).getId(),
                        profiles.get(1).getId(),
                        profiles.get(2).getId(),
                        profiles.get(3).getId()
                )
                .build(4);

        userRepository.save(users);

        users.get(0).setAffiliations(
                newAffiliation()
                        .withId()
                        .withAffiliationType(AffiliationType.PROFESSIONAL)
                        .withDescription("Affiliation Description 1")
                        .withExists(TRUE)
                        .withUser(users.get(0))
                        .build(1)
        );

        userRepository.save(users);

        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withUser(users.get(0), users.get(1), users.get(2), users.get(3))
                .withStatus(SENT)
                .build(4);

        saveNewCompetitionParticipants(newAssessorInvites);
        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                null,
                singletonList(PENDING),
                FALSE,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(3, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(3, content.size());
        assertEquals("Anthony Hale", content.get(0).getInvite().getName());
        assertEquals("Charles Dance", content.get(1).getInvite().getName());
        assertEquals("Claire Jenkins", content.get(2).getInvite().getName());
    }

    @Test
    public void getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant_noFilters() throws Exception {
        List<CompetitionInvite> newAssessorInvites = newCompetitionInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withStatus(SENT, SENT, OPENED, OPENED)
                .build(4);

        List<CompetitionAssessmentParticipant> participants = saveNewCompetitionParticipants(newAssessorInvites);
        RejectionReason reason = rejectionReasonRepository.findAll().get(0);
        participants.get(2).reject(reason, Optional.of("too busy"));
        participants.get(3).reject(reason, Optional.of("not well"));

        flushAndClearSession();

        assertEquals(4, repository.count());

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<CompetitionAssessmentParticipant> pagedResult = repository.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competition.getId(),
                null,
                 asList(PENDING, REJECTED),
                null,
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(4, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<CompetitionAssessmentParticipant> content = pagedResult.getContent();

        assertEquals(4, content.size());
        assertEquals("Anthony Hale", content.get(0).getInvite().getName());
        assertEquals(REJECTED, content.get(0).getStatus());
        assertEquals("Charles Dance", content.get(1).getInvite().getName());
        assertEquals(PENDING, content.get(1).getStatus());
        assertEquals("Claire Jenkins", content.get(2).getInvite().getName());
        assertEquals(REJECTED, content.get(2).getStatus());
        assertEquals("Jane Pritchard", content.get(3).getInvite().getName());
        assertEquals(PENDING, content.get(3).getStatus());
    }

    @Test
    public void findAssessorAvailableForAssessmentPanel() {
        CompetitionAssessmentParticipant availableParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        availableParticipant.acceptAndAssignUser(user);

        User assessor = userMapper.mapToDomain(getFelixWilson());
        CompetitionAssessmentParticipant unavailableParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName(assessor.getName())
                        .withEmail(assessor.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(assessor)
                        .build()
        );
        unavailableParticipant.acceptAndAssignUser(assessor);

        AssessmentPanelInvite invite = new AssessmentPanelInvite(userMapper.mapToDomain(getFelixWilson()), "hash", competition);
        assessmentPanelInviteRepository.save(invite);

        flushAndClearSession();

        List<CompetitionAssessmentParticipant> retrievedParticipants = repository.findParticipantsNotOnPanel(competition.getId());
        assertEquals(1, retrievedParticipants.size());
        assertEqualParticipants(availableParticipant, retrievedParticipants.get(0));
    }
}
