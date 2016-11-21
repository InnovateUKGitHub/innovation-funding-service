package com.worth.ifs.assessment.controller;


import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static com.worth.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static com.worth.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static com.worth.ifs.invite.domain.ParticipantStatus.PENDING;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
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

    private Competition competition;

    @Before
    public void setup() {
        loginSystemRegistrationUser();

        competition = competitionRepository.findOne(1L);
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
}
