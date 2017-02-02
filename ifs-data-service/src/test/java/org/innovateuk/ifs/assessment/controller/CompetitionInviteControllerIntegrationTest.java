package org.innovateuk.ifs.assessment.controller;


import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

public class CompetitionInviteControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionInviteController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionInviteController controller) {
        this.controller = controller;
    }

    @Autowired
    private CompetitionInviteRepository competitionInviteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Competition competition;
    private Profile profile;

    @Before
    public void setup() {
        loginSystemRegistrationUser();

        competition = competitionRepository.findOne(1L);
        profile = profileRepository.save(newProfile().with(id(null)).build());
        User user = userRepository.findByEmail("paul.plum@gmail.com").get();
        user.setProfileId(profile.getId());
    }

    @Test
    public void getCreatedInvite() {
        InnovationArea innovationArea = newInnovationArea().withName("innovation area").build();
        long createdId = competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("tom poly")
                .withEmail("tom@poly.io")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .withStatus(InviteStatus.CREATED)
                .withInnovationArea(innovationArea)
                .build())
                .getId();

        loginCompAdmin();

        RestResult<AssessorInviteToSendResource> serviceResult = controller.getCreatedInvite(createdId);
        assertTrue(serviceResult.isSuccess());

        AssessorInviteToSendResource inviteResource = serviceResult.getSuccessObjectOrThrowException();
        assertEquals("Connected digital additive manufacturing", inviteResource.getCompetitionName());
        assertEquals("tom poly", inviteResource.getRecipient());
    }

    @Test
    public void getInvite() {
        competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("tom poly")
                .withEmail("tom@poly.io")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .build());

        RestResult<CompetitionInviteResource> serviceResult = controller.getInvite("hash");
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource inviteResource = serviceResult.getSuccessObjectOrThrowException();
        assertEquals("Connected digital additive manufacturing", inviteResource.getCompetitionName());
    }

    @Test
    public void getInvite_hashNotExists() {
        RestResult<CompetitionInviteResource> serviceResult = controller.getInvite("not exists hash");
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionInvite.class, "not exists hash")));
    }

    @Test
    public void openInvite() {
        competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("name")
                .withEmail("tom@poly.io")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .build());

        RestResult<CompetitionInviteResource> serviceResult = controller.openInvite("hash");
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource inviteResource = serviceResult.getSuccessObjectOrThrowException();
        assertEquals("Connected digital additive manufacturing", inviteResource.getCompetitionName());
    }

    @Test
    public void openInvite_hashNotExists() {
        RestResult<CompetitionInviteResource> serviceResult = controller.openInvite("not exists hash");
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionInvite.class, "not exists hash")));
    }

    @Test
    public void checkExistingUser_userExistsOnInvite() {
        User user = userRepository.save(newUser()
                .with(id(null))
                .withEmailAddress("tom@poly.io")
                .withUid("a36c4aff-7840-4cd8-b5dd-5c945b8d9959")
                .build());

        // Save an invite for the User
        competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("name")
                .withEmail("tom@poly.io")
                .withUser(user)
                .withHash("hash")
                .withCompetition(competition)
                .build());

        RestResult<Boolean> serviceResult = controller.checkExistingUser("hash");
        assertTrue(serviceResult.isSuccess());

        Boolean existingUser = serviceResult.getSuccessObjectOrThrowException();
        assertTrue(existingUser);
    }

    @Test
    public void checkExistingUser_userExistsForEmail() {
        userRepository.save(newUser()
                .with(id(null))
                .withEmailAddress("user-exists@for-this.address")
                .withUid("a36c4aff-7840-4cd8-b5dd-5c945b8d9959")
                .build());

        // Save an invite without a User but with an e-mail address for which a User exists
        competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("name")
                .withEmail("user-exists@for-this.address")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .build());

        RestResult<Boolean> serviceResult = controller.checkExistingUser("hash");
        assertTrue(serviceResult.isSuccess());

        Boolean existingUser = serviceResult.getSuccessObjectOrThrowException();
        assertTrue(existingUser);
    }

    @Test
    public void checkExistingUser_userNotExists() {
        // Save an invite without a User and with an e-mail address for which no User exists
        competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("name")
                .withEmail("no-user-exists@for-this.address")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .build());


        RestResult<Boolean> serviceResult = controller.checkExistingUser("hash");
        assertTrue(serviceResult.isSuccess());

        Boolean existingUser = serviceResult.getSuccessObjectOrThrowException();
        assertFalse(existingUser);
    }

    @Test
    public void checkExistingUser_hashNotExists() {
        RestResult<Boolean> serviceResult = controller.checkExistingUser("not exists hash");
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionInvite.class, "not exists hash")));
    }

    @Test
    public void acceptInvite_participantIsDifferentUser() {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser(newUser().withId(1L))
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .withUser(newUser().withId(1L))
                .build());
        assertTrue(controller.openInvite("hash").isSuccess());
        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void acceptInvite_noParticipantUserAndInviteHasSameEmail() {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser((User[]) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .withInnovationArea(newInnovationArea().build())
                        .build())
                .withUser((User[]) null)
                .build());

        assertTrue(controller.openInvite("hash").isSuccess());
        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void acceptInvite_noParticipantUserAndInviteHasDifferentEmail() {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser((User[]) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .withUser((User[]) null)
                .build());
        assertTrue(controller.openInvite("hash").isSuccess());

        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void acceptInvite() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser(newUser().withId(getPaulPlum().getId()).build())
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());

        assertTrue(controller.openInvite("hash").isSuccess());

        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void acceptInvite_newAssessor() throws Exception {
        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withHash("hash")
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .build())
                .build());

        assertTrue(controller.openInvite("hash").isSuccess());

        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");
        assertTrue(serviceResult.isSuccess());

        assertEquals(Collections.singleton(innovationArea), profile.getInnovationAreas());
    }

    @Test
    public void acceptInvite_hashNotExists() throws Exception {
        loginPaulPlum();
        RestResult<Void> serviceResult = controller.acceptInvite("hash not exists");
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void acceptInvite_notOpened() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());

        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "Connected digital additive manufacturing")));
    }

    @Test
    public void acceptInvite_rejected() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());
        controller.openInvite("hash");


        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(1L).build(), "too busy");
        RestResult<Void> serviceResult = controller.rejectInvite("hash", competitionRejectionResource);
        assertTrue(serviceResult.isSuccess());

        loginPaulPlum();
        RestResult<Void> acceptResult = controller.acceptInvite("hash");

        assertTrue(acceptResult.isFailure());
        assertTrue(acceptResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "Connected digital additive manufacturing")));
    }

    @Test
    public void rejectInvite() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());
        controller.openInvite("hash");

        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(1L).build(), "too busy");
        RestResult<Void> serviceResult = controller.rejectInvite("hash", competitionRejectionResource);
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());
        controller.openInvite("hash");

        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(1L).build(), null);
        RestResult<Void> serviceResult = controller.rejectInvite("hash", competitionRejectionResource);
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void rejectInvite_accepted() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("paul.plum@gmail.com")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .withInnovationArea(newInnovationArea().build())
                        .build())
                .build());
        controller.openInvite("hash");

        loginPaulPlum();

        RestResult<Void> serviceResult = controller.acceptInvite("hash");
        assertTrue(serviceResult.isSuccess());

        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(1L).build(), "too busy");

        loginSystemRegistrationUser();
        RestResult<Void> rejectResult = controller.rejectInvite("hash", competitionRejectionResource);

        assertTrue(rejectResult.isFailure());
        assertTrue(rejectResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "Connected digital additive manufacturing")));
    }

    @Test
    public void rejectInvite_notOpened() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());

        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(1L).build(), "too busy");
        RestResult<Void> serviceResult = controller.rejectInvite("hash", competitionRejectionResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "Connected digital additive manufacturing")));
    }

    @Test
    public void rejectInvite_unknownReason() throws Exception {
        competitionParticipantRepository.save(newCompetitionParticipant()
                .with(id(null))
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .withCompetition(competition)
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("no-user-exists@for-this.address")
                        .withUser((User) null)
                        .withHash("hash")
                        .withCompetition(competition)
                        .build())
                .build());
        controller.openInvite("hash");

        CompetitionRejectionResource competitionRejectionResource =
                new CompetitionRejectionResource(newRejectionReasonResource().withId(-1L).build(), "too busy");
        RestResult<Void> serviceResult = controller.rejectInvite("hash", competitionRejectionResource);
        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(RejectionReason.class, -1L)));
    }

    @Test
    public void inviteNewUser() throws Exception {
        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);

        NewUserStagedInviteResource newUserStagedInvite = newNewUserStagedInviteResource()
                .withName("new user name")
                .withEmail("no-other-user-exists@for-this.address")
                .withCompetitionId(competition.getId())
                .withInnovationAreaId(innovationArea.getId())
                .build();

        loginCompAdmin();
        RestResult<CompetitionInviteResource> serviceResult = controller.inviteNewUser(newUserStagedInvite);

        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource resource = serviceResult.getSuccessObjectOrThrowException();

        assertEquals(competition.getName(), resource.getCompetitionName());
        assertEquals(CREATED, resource.getStatus());
    }

    @Test
    public void inviteNewUsers() throws Exception {
        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);

        NewUserStagedInviteListResource newUserInvites = buildNewUserInviteList(competition.getId(), innovationArea.getId());

        loginCompAdmin();
        RestResult<Void> serviceResult = controller.inviteNewUsers(newUserInvites, competition.getId());

        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void inviteNewUsers_competitionNotFound() throws Exception {
        long competitionId = 10000L;
        assertNull(competitionRepository.findOne(competitionId));

        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);

        loginCompAdmin();
        NewUserStagedInviteListResource newUserInvites = buildNewUserInviteList(competitionId, innovationArea.getId());
        RestResult<Void> serviceResult = controller.inviteNewUsers(newUserInvites, competitionId);

        assertFalse(serviceResult.isSuccess());
        assertTrue(serviceResult.getFailure().is(notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void inviteNewUsers_innovationAreaNotFound() throws Exception {
        long innovationAreaId = 10000L;
        assertNull(innovationAreaRepository.findOne(innovationAreaId));

        loginCompAdmin();
        NewUserStagedInviteListResource newUserInvites = buildNewUserInviteList(competition.getId(), innovationAreaId);
        RestResult<Void> serviceResult = controller.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(2, serviceResult.getFailure().getErrors().size());
        assertEquals("invites[0].innovationArea", serviceResult.getFailure().getErrors().get(0).getFieldName());
        assertEquals(innovationAreaId, serviceResult.getFailure().getErrors().get(0).getFieldRejectedValue());
        assertEquals("invites[1].innovationArea", serviceResult.getFailure().getErrors().get(1).getFieldName());
        assertEquals(innovationAreaId, serviceResult.getFailure().getErrors().get(1).getFieldRejectedValue());
    }

    @Test
    public void inviteNewUsers_userExists() throws Exception {
        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);

        competitionInviteRepository.save(new CompetitionInvite("Test Name 1", "testname1@for-this.address", "hash", competition, innovationArea));

        loginCompAdmin();
        NewUserStagedInviteListResource newUserInvites = buildNewUserInviteList(competition.getId(), innovationArea.getId());
        RestResult<Void> serviceResult = controller.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(1, serviceResult.getFailure().getErrors().size());
        assertEquals("invites[0].email", serviceResult.getFailure().getErrors().get(0).getFieldName());
        assertEquals("testname1@for-this.address", serviceResult.getFailure().getErrors().get(0).getFieldRejectedValue());
    }

    private NewUserStagedInviteListResource buildNewUserInviteList(long competitionId, long innovationAreaId) {
        return new NewUserStagedInviteListResource(
                newNewUserStagedInviteResource()
                        .withName("Test Name 1", "Test Name 2")
                        .withEmail("testname1@for-this.address", "testname2@for-this.address")
                        .withCompetitionId(competitionId)
                        .withInnovationAreaId(innovationAreaId)
                        .build(2)
        );
    }

    @Test
    public void sendInvite() throws Exception {
        long createdId = competitionInviteRepository.save(newCompetitionInvite()
                .with(id(null))
                .withName("tom poly")
                .withEmail("tom@poly.io")
                .withUser((User) null)
                .withHash("hash")
                .withCompetition(competition)
                .withStatus(InviteStatus.CREATED)
                .build())
                .getId();
        EmailContent content = newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain")
                .withHtmlText("html")
                .build();

        loginCompAdmin();

        RestResult<AssessorInviteToSendResource> serviceResult = controller.sendInvite(createdId, content);
        assertTrue(serviceResult.isSuccess());
    }
}
