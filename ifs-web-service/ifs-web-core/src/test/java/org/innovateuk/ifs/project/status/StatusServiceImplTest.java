package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.service.ProjectStatusRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatusServiceImplTest {

    @InjectMocks
    private StatusServiceImpl service;

    @Mock
    private ProjectStatusRestService projectStatusRestService;

    @Test
    public void testGetProjectTeamStatus() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectStatusRestService.getProjectTeamStatus(1L, Optional.empty())).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.empty());

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectStatusRestService).getProjectTeamStatus(1L, Optional.empty());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectStatusRestService.getProjectTeamStatus(1L, Optional.of(456L))).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.of(456L));

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectStatusRestService).getProjectTeamStatus(1L, Optional.of(456L));
    }
}