package org.innovateuk.ifs.finance.controller;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceRowControllerTest extends BaseControllerMockMVCTest<FinanceRowController> {

    @Mock
    private ValidationUtil validationUtil;
    
    @Test
    public void addShouldCreateNewCost() throws Exception{

        when(financeRowServiceMock.addCost(123L, 456L, null)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(post("/cost/add/{applicationFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCost(123L, 456L, null);
    }
    
    @Test
    public void addShouldCreateNewCostWithoutPersisting() throws Exception{

        when(financeRowServiceMock.addCostWithoutPersisting(123L, 456L)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(post("/cost/add-without-persisting/{applicationFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCostWithoutPersisting(123L, 456L);
    }

    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(applicationFinanceRowRepositoryMock);
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

        verifyNoMoreInteractions(applicationFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(financeRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceFailure(notFoundError(FinanceRowMetaField.class, 123L)));

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(financeRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(financeRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));

        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(financeRowServiceMock, times(1)).updateCost(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(financeRowServiceMock.deleteCost(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/cost/delete/123"))
                .andExpect(status().isNoContent());

        verify(financeRowServiceMock, times(1)).deleteCost(123L);
    }

    @Override
    protected FinanceRowController supplyControllerUnderTest() {
        return new FinanceRowController();
    }
}
