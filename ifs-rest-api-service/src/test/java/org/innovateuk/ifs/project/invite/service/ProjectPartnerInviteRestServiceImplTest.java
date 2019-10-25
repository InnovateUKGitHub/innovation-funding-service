package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.sentProjectPartnerInviteResourceListType;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void getPartnerInvites() {
        long projectId = 1L;
        List<SentProjectPartnerInviteResource> invites = newSentProjectPartnerInviteResource().build(1);
        setupGetWithRestResultExpectations(format("/project/%d/project-partner-invite", projectId), sentProjectPartnerInviteResourceListType(), invites);

        RestResult<List<SentProjectPartnerInviteResource>> result = service.getPartnerInvites(projectId);

        assertTrue(result.isSuccess());
        assertEquals(invites, result.getSuccess());
    }

    @Test
    public void resendInvite() {
        long projectId = 1L;
        long inviteId = 2L;
        setupPostWithRestResultExpectations(format("/project/%d/project-partner-invite/%d/resend", projectId, inviteId), HttpStatus.OK);

        RestResult<Void> result = service.resendInvite(projectId, inviteId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteInvite() {
        long projectId = 1L;
        long inviteId = 2L;
        setupDeleteWithRestResultExpectations(format("/project/%d/project-partner-invite/%d", projectId, inviteId), HttpStatus.OK);

        RestResult<Void> result = service.deleteInvite(projectId, inviteId);

        assertTrue(result.isSuccess());
    }

    @Override
    protected ProjectPartnerInviteRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectPartnerInviteRestServiceImpl();
    }
}