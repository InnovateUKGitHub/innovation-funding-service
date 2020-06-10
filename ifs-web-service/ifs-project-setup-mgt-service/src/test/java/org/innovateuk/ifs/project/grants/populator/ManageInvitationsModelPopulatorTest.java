package org.innovateuk.ifs.project.grants.populator;

import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ManageInvitationsModelPopulatorTest {

    private ManageInvitationsModelPopulator populator;

    @Before
    public void setUp() {
        populator = new ManageInvitationsModelPopulator();
    }

    @Test
    public void shouldPopulate() {
        // given
        ProjectResource project = ProjectResourceBuilder.newProjectResource()
                .withCompetition(4L)
                .withCompetitionName("compName")
                .withId(5L)
                .withName("projName")
                .withApplication(6L).build();
        List<SentGrantsInviteResource> grants = new ArrayList<>();

        // when
        ManageInvitationsViewModel result = populator.populateManageInvitationsViewModel(project, grants);

        // then
        assertEquals(4L, result.getCompetitionId());
        assertEquals("compName", result.getCompetitionName());
        assertEquals(Long.valueOf(5L), result.getProjectId());
        assertEquals("projName", result.getProjectName());
        assertEquals(6L, result.getApplicationId());
        assertEquals(grants, result.getGrants());


    }
}
