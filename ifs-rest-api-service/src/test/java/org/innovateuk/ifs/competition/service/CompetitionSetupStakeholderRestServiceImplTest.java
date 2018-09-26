package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertTrue;

public class CompetitionSetupStakeholderRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionSetupStakeholderRestServiceImpl> {

    private static final String competitionSetupStakeholderRestURL = "/competition/setup/";

    @Override
    protected CompetitionSetupStakeholderRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupStakeholderRestServiceImpl();
    }

    @Test
    public void inviteStakeholder() {
        long competitionId = 1L;
        InviteUserResource inviteUserResource = new InviteUserResource();

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/invite";
        setupPostWithRestResultExpectations(url, inviteUserResource, HttpStatus.OK);

        RestResult<Void> result = service.inviteStakeholder(inviteUserResource, competitionId);
        assertTrue(result.isSuccess());
    }
}

