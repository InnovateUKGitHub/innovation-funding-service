package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewBuilder.newInterview;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInviteWithoutId;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class InterviewParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InterviewParticipantRepository> {

    private Competition competition;
    private InnovationArea innovationArea;
    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    @Override
    protected void setRepository(InterviewParticipantRepository repository) {
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
        List<InterviewParticipant> savedParticipants = saveNewInterviewParticipants(
                newInterviewInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(SENT)
                        .build(2)
        );
        flushAndClearSession();

        List<InterviewParticipant> retrievedParticipants = repository.findAll();

        assertEquals(10, retrievedParticipants.size());  // Including 8 pre-existing participants added via patch
        assertEqualParticipants(savedParticipants, retrievedParticipants);
    }

    @Test
    public void save() {
        InterviewParticipant savedParticipant = saveNewInterviewParticipant(
                newInterviewInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(SENT)
                        .build()
        );
        flushAndClearSession();

        long id = savedParticipant.getId();

        InterviewParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_accepted() {
        InterviewParticipant savedParticipant = saveNewInterviewParticipant(
                newInterviewInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        savedParticipant.acceptAndAssignUser(user);
        flushAndClearSession();

        long id = savedParticipant.getId();

        InterviewParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_rejected() {
        InterviewParticipant savedParticipant = saveNewInterviewParticipant(
                newInterviewInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(OPENED)
                        .build()
        );

        savedParticipant.reject();
        flushAndClearSession();

        long id = savedParticipant.getId();

        InterviewParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getAssessorsByCompetitionAndStatus() throws Exception {
        loginSteveSmith();

        User acceptedUser = newUser()
                .withId()
                .withEmailAddress("ah@test2.com")
                .withUid("uid-1")
                .withFirstName("Anthony")
                .withLastName("Hale")
                .withProfileId()
                .build();

        userRepository.save(acceptedUser);

        List<InterviewInvite> newAssessorInvites = newInterviewInviteWithoutId()
                .withName("Jane Pritchard", "Charles Dance", "Claire Jenkins", "Anthony Hale")
                .withEmail("jp@test.com", "cd@test.com", "cj@test.com", "ah@test2.com")
                .withCompetition(competition)
                .withStatus(SENT)
                .build(4);

        List<InterviewParticipant> InterviewParticipants = saveNewInterviewParticipants(newAssessorInvites);

        InterviewParticipants.get(3).getInvite().open();
        InterviewParticipants.get(3).acceptAndAssignUser(acceptedUser);

        repository.save(InterviewParticipants);
        flushAndClearSession();

        assertEquals(12, repository.count()); // Including 8 pre-existing participants added via patch
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<InterviewParticipant> pagedResult = repository.getInterviewPanelAssessorsByCompetitionAndStatusContains(
                competition.getId(),
                singletonList(ACCEPTED),
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<InterviewParticipant> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Anthony Hale", content.get(0).getInvite().getName());
    }

    @Test
    public void getInterviewAcceptedAssessors() throws Exception {
        loginSteveSmith();

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.save(profiles);

        User user = newUser()
                .withId()
                .withEmailAddress("test@test.co.uk")
                .withUid("uid-1")
                .withFirstName("Kieran")
                .withLastName("Hester")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .build();

        userRepository.save(user);

        List<InterviewInvite> newAssessorInvites = newInterviewInviteWithoutId()
                .withUser(user)
                .withName(user.getFirstName() + " " + user.getLastName())
                .withEmail(user.getEmail())
                .withCompetition(competition)
                .withStatus(SENT)
                .build(1);

        Application application = newApplication().withCompetition(competition).build();

        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole().withApplication(application).withUser(user).build();

        processRoleRepository.save(processRole);

        Interview interview = newInterview().withTarget(application).withState(InterviewState.ASSIGNED).withParticipant(processRole).build();

        interviewRepository.save(interview);

        List<InterviewParticipant> InterviewParticipants = saveNewInterviewParticipants(newAssessorInvites);

        InterviewParticipants.get(0).getInvite().open();
        CompetitionParticipantRole role = InterviewParticipants.get(0).getRole();
        InterviewParticipants.get(0).acceptAndAssignUser(user);

        repository.save(InterviewParticipants);
        flushAndClearSession();

        assertEquals(9, repository.count()); // Including 8 pre-existing participants added via patch
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        Page<InterviewAcceptedAssessorsResource> pagedResult = repository.getInterviewAcceptedAssessorsByCompetition(
                competition.getId(),
                pageable
        );

        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1, pagedResult.getTotalElements());
        assertEquals(20, pagedResult.getSize());
        assertEquals(0, pagedResult.getNumber());

        List<InterviewAcceptedAssessorsResource> content = pagedResult.getContent();

        assertEquals(1, content.size());
        assertEquals("Kieran Hester", content.get(0).getName());
        assertEquals(1L, content.get(0).getNumberOfAllocatedApplications());
    }



    @Test
    public void findByUserIdAndRole() {
        loginSteveSmith();

        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        List<InterviewParticipant> savedParticipants = saveNewInterviewParticipants(
                newInterviewInviteWithoutId()
                        .withName("name1")
                        .withHash(Invite.generateInviteHash())
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build(1)
        );

        savedParticipants.get(0).acceptAndAssignUser(user);
        interviewParticipantRepository.save( savedParticipants.get(0));

        flushAndClearSession();

        List<InterviewParticipant> retrievedParticipants = repository.findByUserIdAndRole(user.getId(), INTERVIEW_ASSESSOR);
        assertEquals(1, retrievedParticipants.size());
        assertEqualParticipants(savedParticipants, retrievedParticipants);
    }

    @Test
    public void findByUserIdAndCompetitionId() {
        loginSteveSmith();

        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        User user = newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("foo")
                .build();

        userRepository.save(user);

        InterviewParticipant savedParticipant = saveNewInterviewParticipant(
                newInterviewInviteWithoutId()
                        .withName("name1")
                        .withHash(Invite.generateInviteHash())
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        savedParticipant.acceptAndAssignUser(user);

        interviewParticipantRepository.save(savedParticipant);
        flushAndClearSession();

        InterviewParticipant retrievedParticipant = interviewParticipantRepository.findByUserIdAndCompetitionIdAndRole(user.getId(), competition.getId(), CompetitionParticipantRole.INTERVIEW_ASSESSOR);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    private InterviewParticipant saveNewInterviewParticipant(InterviewInvite invite) {
        return repository.save(new InterviewParticipant(invite));
    }

    private List<InterviewParticipant> saveNewInterviewParticipants(List<InterviewInvite> invites) {
        return invites.stream().map(assessmentPanelInvite ->
                repository.save(new InterviewParticipant(assessmentPanelInvite))).collect(toList());
    }

    private void assertEqualParticipants(List<InterviewParticipant> expected, List<InterviewParticipant> actual) {
        List<InterviewParticipant> subList = actual.subList(actual.size() - expected.size(), actual.size()); // Exclude pre-existing participants added via patch
        zip(expected, subList, this::assertEqualParticipants);
    }

    private void assertEqualParticipants(InterviewParticipant expected, InterviewParticipant actual) {
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
}
