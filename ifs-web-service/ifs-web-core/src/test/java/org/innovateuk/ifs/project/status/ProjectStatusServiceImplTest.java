package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.service.ProjectStatusRestService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectStatusServiceImplTest extends BaseServiceUnitTest<ProjectStatusService> {
    @Mock
    ProjectStatusRestService projectStatusRestService;

    @Override
    protected ProjectStatusService supplyServiceUnderTest() {
        return new ProjectStatusServiceImpl();
    }

    @Test
    public void testGetCompetitionStatus(){
        Long competitionId = 123L;
        CompetitionProjectsStatusResource competitionProjectsStatusResource = newCompetitionProjectsStatusResource().build();
        when(projectStatusRestService.getCompetitionStatus(competitionId)).thenReturn(RestResult.restSuccess(competitionProjectsStatusResource));
        CompetitionProjectsStatusResource result = service.getCompetitionStatus(competitionId);
        assertEquals(competitionProjectsStatusResource, result);
    }
}
