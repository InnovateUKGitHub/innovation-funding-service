package org.innovateuk.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceRowControllerTest extends BaseControllerMockMVCTest<ProjectFinanceRowController> {

    @Mock
    private ValidationUtil validationUtil;

    @Override
    protected ProjectFinanceRowController supplyControllerUnderTest() {
        return new ProjectFinanceRowController();
    }

    @Test
    public void addShouldCreateNewCost() throws Exception{

        when(projectFinanceRowServiceMock.addCost(123L, 456L, null)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/project/add/{projectFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(projectFinanceRowServiceMock, times(1)).addCost(123L, 456L, null);
    }

    @Test
    public void addProjectCostWithoutPersisting() throws Exception{

        when(projectFinanceRowServiceMock.addCostWithoutPersisting(123L, 456L)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/project/add-without-persisting/{projectFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(projectFinanceRowServiceMock, times(1)).addCostWithoutPersisting(123L, 456L);
    }

    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(projectFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnBadRequestOnWrongContentType() throws Exception {
        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.IMAGE_GIF))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoMoreInteractions(projectFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(projectFinanceRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceFailure(notFoundError(FinanceRowMetaField.class, 123L)));

        mockMvc.perform(get("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(projectFinanceRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(projectFinanceRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));

        mockMvc.perform(post("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(projectFinanceRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(projectFinanceRowServiceMock.deleteCost(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/cost/project/delete/123"))
                .andExpect(status().isNoContent());

        verify(projectFinanceRowServiceMock, times(1)).deleteCost(123L);
    }
}
