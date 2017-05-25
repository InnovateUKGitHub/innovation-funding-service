package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
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
    private ProjectRestService projectRestService;

    @Test
    public void testGetProjectTeamStatus() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectRestService.getProjectTeamStatus(1L, Optional.empty())).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.empty());

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectRestService).getProjectTeamStatus(1L, Optional.empty());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectRestService.getProjectTeamStatus(1L, Optional.of(456L))).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.of(456L));

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectRestService).getProjectTeamStatus(1L, Optional.of(456L));
    }
}