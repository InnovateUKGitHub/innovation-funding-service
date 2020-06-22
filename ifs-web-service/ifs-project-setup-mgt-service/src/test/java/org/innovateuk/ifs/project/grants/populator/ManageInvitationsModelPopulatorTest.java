package org.innovateuk.ifs.project.grants.populator;

import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.builder.SentGrantsInviteResourceBuilder;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.grantsinvite.builder.SentGrantsInviteResourceBuilder.newSentGrantsInviteResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageInvitationsModelPopulatorTest {

    @InjectMocks
    private ManageInvitationsModelPopulator populator;

    @Mock
    private ProjectService projectService;

    @Mock
    private GrantsInviteRestService grantsInviteRestService;

    @Test
    public void shouldPopulate() {
        // given
        Long projectId = 5L;
        ProjectResource project = ProjectResourceBuilder.newProjectResource()
                .withCompetition(4L)
                .withCompetitionName("compName")
                .withId(projectId)
                .withName("projName")
                .withApplication(6L).build();

        when(projectService.getById(projectId)).thenReturn(project);

        List<SentGrantsInviteResource> grants = newSentGrantsInviteResource()
                .withStatus(InviteStatus.SENT, InviteStatus.CREATED, InviteStatus.SENT, InviteStatus.OPENED)
                .build(4);
        when(grantsInviteRestService.getAllForProject(projectId)).thenReturn(restSuccess(grants));

        // when
        ManageInvitationsViewModel result = populator.populateManageInvitationsViewModel(projectId);

        // then
        assertEquals(4L, result.getCompetitionId());
        assertEquals("compName", result.getCompetitionName());
        assertEquals(projectId, result.getProjectId());
        assertEquals("projName", result.getProjectName());
        assertEquals(6L, result.getApplicationId());
        assertEquals(Arrays.asList(grants.get(0), grants.get(2)), result.getGrants());
    }

}
