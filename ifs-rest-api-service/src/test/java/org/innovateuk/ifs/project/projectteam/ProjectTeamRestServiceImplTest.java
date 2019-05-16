package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectTeamRestServiceImplTest extends BaseRestServiceUnitTest<ProjectTeamRestServiceImpl> {

    @Test
    public void inviteProjectMember() {
        long projectId = 1L;
        ProjectUserInviteResource projectUserInviteResource = newProjectUserInviteResource().build();
        setupPostWithRestResultExpectations(format("/project/%d/team/invite", projectId), projectUserInviteResource, HttpStatus.OK);

        RestResult<Void> result = service.inviteProjectMember(projectId, projectUserInviteResource);

        assertTrue(result.isSuccess());
        setupPostWithRestResulVerifications(format("/project/%d/team/invite", projectId), projectUserInviteResource);
    }

    @Test
    public void removeUser() {
        long projectId = 654L;
        long userId = 987L;
        setupPostWithRestResultExpectations(format("/project/%d/team/remove-user/%d", projectId, userId), null, OK);
        RestResult<Void> result = service.removeUser(projectId, userId);
        setupPostWithRestResultVerifications(format("/project/%d/team/remove-user/%d", projectId, userId), Void.class);
        assertTrue(result.isSuccess());
    }

    @Test
    public void removeInvite() {
        long projectId = 456L;
        long inviteId = 789L;
        setupPostWithRestResultExpectations(
                format("/project/%d/team/remove-invite/%d", projectId, inviteId), null, OK);
        RestResult<Void> result = service.removeInvite(projectId, inviteId);
        setupPostWithRestResultVerifications(
                format("/project/%d/team/remove-invite/%d", projectId, inviteId), Void.class);

        assertTrue(result.isSuccess());
    }

    @Override
    protected ProjectTeamRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectTeamRestServiceImpl();
    }
}