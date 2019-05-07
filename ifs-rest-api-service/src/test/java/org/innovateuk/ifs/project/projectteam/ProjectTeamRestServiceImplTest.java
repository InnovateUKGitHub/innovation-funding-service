package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectTeamRestServiceImplTest extends BaseRestServiceUnitTest<ProjectTeamRestServiceImpl> {

    @Test
    public void inviteProjectMember() {
        long projectId = 1L;
        ProjectUserInviteResource projectUserInviteResource = newProjectUserInviteResource().build();
        setupPostWithRestResultExpectations(String.format("/project/%d/team/invite", projectId), projectUserInviteResource, HttpStatus.OK);

        RestResult<Void> result = service.inviteProjectMember(projectId, projectUserInviteResource);

        assertTrue(result.isSuccess());
        setupPostWithRestResulVerifications(String.format("/project/%d/team/invite", projectId), projectUserInviteResource);
    }


    @Test
    public void removeUser() {
        long projectId = 654L;
        long userId = 987L;
        setupPostWithRestResultExpectations(String.format("/project/%d/remove-user/%d", projectId, userId), null, OK);
        RestResult<Void> result = service.removeUser(projectId, userId);
        setupPostWithRestResultVerifications(String.format("/project/%d/remove-user/%d", projectId, userId), Void.class);
        assertTrue(result.isSuccess());
    }

    @Override
    protected ProjectTeamRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectTeamRestServiceImpl();
    }
}