package com.worth.ifs.assessment.controller;


import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionInviteControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionInviteController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionInviteController controller) {
        this.controller = controller;
    }


    @Autowired
    private CompetitionInviteRepository competitionInviteRepository;

    @Autowired
    CompetitionRepository competitionRepository;

    @Before
    public void setup() {
        swapOutForUser(getSystemRegistrationUser());

        Competition competition = competitionRepository.findOne(1L);

        CompetitionInvite i = new CompetitionInvite("name", "tom@poly.io", "hash", competition);
        competitionInviteRepository.save(i);
        flushAndClearSession();
    }

    @Test
    public void getInvite() {
        RestResult<CompetitionInviteResource> serviceResult = controller.getInvite("hash");
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource inviteResource = serviceResult.getSuccessObjectOrThrowException();
        assertEquals("Connected digital additive manufacturing", inviteResource.getCompetitionName());
    }

    @Test
    public void openInvite() {
        RestResult<CompetitionInviteResource> serviceResult = controller.openInvite("hash");
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource inviteResource = serviceResult.getSuccessObjectOrThrowException();
        assertEquals("Connected digital additive manufacturing", inviteResource.getCompetitionName());
    }

    @Test
    public void openInvite_hashNotExists() {
        assertTrue(controller.openInvite("not exists hash").isFailure());
    }
}
