package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
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
    UserRepository userRepository;

    @Autowired
    CompetitionRepository competitionRepository;

    private Competition competition;

    @Before
    public void setup() {
        swapOutForUser(getSystemRegistrationUser());

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
}
