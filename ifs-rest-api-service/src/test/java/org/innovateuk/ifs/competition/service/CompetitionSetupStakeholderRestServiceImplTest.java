package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void findStakeholders() {

        long competitionId = 1L;
        List<UserResource> responseBody = UserResourceBuilder.newUserResource().build(2);

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/find-all";
        setupGetWithRestResultExpectations(url, userListType(), responseBody);

        List<UserResource> response = service.findStakeholders(competitionId).getSuccess();
        assertEquals(responseBody, response);
    }
}

