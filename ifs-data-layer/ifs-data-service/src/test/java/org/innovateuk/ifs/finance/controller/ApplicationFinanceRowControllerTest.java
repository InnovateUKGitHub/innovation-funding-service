package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceRowControllerTest extends BaseControllerMockMVCTest<ApplicationFinanceRowController> {

    @Mock
    private FinanceValidationUtil validationUtil;

    @Mock
    private ApplicationFinanceRowService financeRowCostsServiceMock;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;
    
    @Test
    public void create() throws Exception {
        FinanceRowItem financeRowItem = new GrantClaimPercentage(1L);

        when(financeRowCostsServiceMock.create(1L, financeRowItem)).thenReturn(serviceSuccess(new GrantClaimPercentage(1L)));

        mockMvc.perform(post("/application-finance-row")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financeRowItem)))
                .andExpect(status().isCreated());

        verify(financeRowCostsServiceMock, times(1)).create(1L, financeRowItem);
    }
    
    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(applicationFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnBadRequestOnWrongContentType() throws Exception {
        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.IMAGE_GIF))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoMoreInteractions(applicationFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {

        GrantClaimPercentage costItem = new GrantClaimPercentage(1L);
        when(financeRowCostsServiceMock.update(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceFailure(notFoundError(FinanceRowMetaField.class, 123L)));

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(financeRowCostsServiceMock, times(1)).update(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        GrantClaimPercentage costItem = new GrantClaimPercentage(1L);
        when(financeRowCostsServiceMock.update(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));

        mockMvc.perform(put("/application-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(financeRowCostsServiceMock, times(1)).update(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(financeRowCostsServiceMock.delete(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/application-finance-row/123"))
                .andExpect(status().isNoContent());

        verify(financeRowCostsServiceMock, times(1)).delete(123L);
    }

    @Override
    protected ApplicationFinanceRowController supplyControllerUnderTest() {
        return new ApplicationFinanceRowController();
    }
}
