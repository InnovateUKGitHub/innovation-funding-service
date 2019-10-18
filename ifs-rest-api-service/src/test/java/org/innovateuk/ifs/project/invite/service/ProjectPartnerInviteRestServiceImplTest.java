package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

public class ProjectPartnerInviteRestServiceImplTest extends BaseRestServiceUnitTest<ProjectPartnerInviteRestServiceImpl> {

    @Test
    public void invitePartnerOrganisation() {
        long projectId = 1L;
        SendProjectPartnerInviteResource invite = new SendProjectPartnerInviteResource("", "", "");
        setupPostWithRestResultExpectations(format("/project/%d/project-partner-invite", projectId), invite, HttpStatus.OK);

        RestResult<Void> result = service.invitePartnerOrganisation(projectId, invite);

        assertTrue(result.isSuccess());
        setupPostWithRestResulVerifications(format("/project/%d/project-partner-invite", projectId), invite);
    }

    @Override
    protected ProjectPartnerInviteRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectPartnerInviteRestServiceImpl();
    }
}