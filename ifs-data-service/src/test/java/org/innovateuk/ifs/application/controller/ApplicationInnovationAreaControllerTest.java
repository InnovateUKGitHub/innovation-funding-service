package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationInnovationAreaControllerTest extends BaseControllerMockMVCTest<ApplicationInnovationAreaController> {
    @Override
    protected ApplicationInnovationAreaController supplyControllerUnderTest() {
        return new ApplicationInnovationAreaController();
    }

    @Test
    public void setInnovationArea() throws Exception {
        Long innovationAreaId = 1L;
        Long applicationId = 1L;

        when(applicationInnovationAreaService.setInnovationArea(applicationId, innovationAreaId)).thenReturn(serviceSuccess(newApplication().build()));

        mockMvc.perform(post("/applicationInnovationArea/setInnovationArea/"+applicationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(innovationAreaId)))
                .andExpect(status().isOk());
    }

    @Test
    public void setNoInnovationAreaApplies() throws Exception {
        Long applicationId = 1L;

        when(applicationInnovationAreaService.setNoInnovationAreaApplies(applicationId)).thenReturn(serviceSuccess(newApplication().build()));

        mockMvc.perform(post("/applicationInnovationArea/setNoInnovationAreaApplicable/"+applicationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAvailableInnovationAreas() throws Exception {
        Long applicationId = 1L;

        when(applicationInnovationAreaService.getAvailableInnovationAreas(applicationId)).thenReturn(serviceSuccess(newInnovationArea().build(5)));

        mockMvc.perform(get("/applicationInnovationArea/getAvailableInnovationAreas/"+applicationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}