package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneBuilder.newProjectProcurementMilestoneResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectProcurementMilestoneControllerTest extends BaseControllerMockMVCTest<ProjectProcurementMilestoneController> {

    @Mock
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    @Test
    public void getByProjectIdAndOrganisationId() throws Exception {
        long projectId = 1L;
        long organisationId = 2L;
        List<ProjectProcurementMilestoneResource> resource = newProjectProcurementMilestoneResource()
                .withDeliverable("Deliverable")
                .build(1);

        when(projectProcurementMilestoneService.getByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.get("/project-procurement-milestone/project/{projectId}/organisation/{organisationId}", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].deliverable", is("Deliverable")));

        verify(projectProcurementMilestoneService).getByProjectIdAndOrganisationId(projectId, organisationId);
    }

    @Test
    public void getByProjectId() throws Exception {
        long projectId = 1L;
        List<ProjectProcurementMilestoneResource> resource = newProjectProcurementMilestoneResource()
                .withDeliverable("Deliverable")
                .build(1);

        when(projectProcurementMilestoneService.getByProjectId(projectId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.get("/project-procurement-milestone/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].deliverable", is("Deliverable")));

        verify(projectProcurementMilestoneService).getByProjectId(projectId);
    }

    @Override
    protected ProjectProcurementMilestoneController supplyControllerUnderTest() {
        return new ProjectProcurementMilestoneController();
    }
}