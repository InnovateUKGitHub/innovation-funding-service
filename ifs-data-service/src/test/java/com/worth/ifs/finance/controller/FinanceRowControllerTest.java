package com.worth.ifs.finance.controller;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.validator.util.ValidationUtil;

public class FinanceRowControllerTest extends BaseControllerMockMVCTest<FinanceRowController> {

    @Override
    protected FinanceRowController supplyControllerUnderTest() {
        return new FinanceRowController();
    }

    @Mock
    private FinanceRowService financeRowServiceMock;

    @Mock
    private ValidationUtil validationUtil;
    
    @Test
    public void addShouldCreateNewCost() throws Exception{

        when(financeRowServiceMock.addCost(123L, 456L, null)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/add/{applicationFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCost(123L, 456L, null);
    }
    
    @Test
    public void addShouldCreateNewCostWithoutPersisting() throws Exception{

        when(financeRowServiceMock.addCostWithoutPersisting(123L, 456L)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/add-without-persisting/{applicationFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCostWithoutPersisting(123L, 456L);
    }

    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(financeRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnBadRequestOnWrongContentType() throws Exception {
        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.IMAGE_GIF))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoMoreInteractions(financeRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(financeRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceFailure(notFoundError(FinanceRowMetaField.class, 123L)));

        mockMvc.perform(get("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(financeRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(financeRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));

        mockMvc.perform(post("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(financeRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(financeRowServiceMock.deleteCost(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/cost/delete/123"))
                .andExpect(status().isNoContent());

        verify(financeRowServiceMock, times(1)).deleteCost(123L);
    }

}