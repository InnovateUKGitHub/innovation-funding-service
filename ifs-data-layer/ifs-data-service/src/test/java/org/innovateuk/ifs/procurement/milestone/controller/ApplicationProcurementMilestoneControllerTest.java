package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ApplicationProcurementMilestoneService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneResourceBuilder.newApplicationProcurementMilestoneResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationProcurementMilestoneControllerTest extends BaseControllerMockMVCTest<ApplicationProcurementMilestoneController> {

    @Mock
    private ApplicationProcurementMilestoneService applicationProcurementMilestoneService;

    @Test
    public void getByApplicationIdAndOrganisationId() throws Exception {

        long applicationId = 1L;
        long organisationId = 2L;
        List<ApplicationProcurementMilestoneResource> resource = newApplicationProcurementMilestoneResource()
                .withDeliverable("Deliverable")
                .build(1);

        when(applicationProcurementMilestoneService.getByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.get("/application-procurement-milestone/application/{applicationId}/organisation/{organisationId}", applicationId, organisationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].deliverable", is("Deliverable")));

        verify(applicationProcurementMilestoneService).getByApplicationIdAndOrganisationId(applicationId, organisationId);
    }

    @Test
    public void findMaxMilestoneMonth() throws Exception {

        long applicationId = 1L;
        when(applicationProcurementMilestoneService.findMaxMilestoneMonth(applicationId)).thenReturn(serviceSuccess(Optional.of(12)));

        mockMvc.perform(MockMvcRequestBuilders.get("/application-procurement-milestone/application/{applicationId}/max-milestone-month", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));

        verify(applicationProcurementMilestoneService).findMaxMilestoneMonth(applicationId);
    }

    @Override
    protected ApplicationProcurementMilestoneController supplyControllerUnderTest() {
        return new ApplicationProcurementMilestoneController();
    }
}