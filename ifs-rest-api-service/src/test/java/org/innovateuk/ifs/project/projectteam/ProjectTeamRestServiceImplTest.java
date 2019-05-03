package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.junit.Assert.assertTrue;

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

    @Override
    protected ProjectTeamRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectTeamRestServiceImpl();
    }
}