package com.worth.ifs.project.status;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.service.ProjectStatusRestService;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
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
