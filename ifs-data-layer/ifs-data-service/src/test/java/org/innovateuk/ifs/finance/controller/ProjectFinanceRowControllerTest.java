package org.innovateuk.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceRowControllerTest extends BaseControllerMockMVCTest<ProjectFinanceRowController> {

    @Mock
    private FinanceValidationUtil validationUtil;

    @Mock
    private ProjectFinanceRowService projectFinanceRowServiceMock;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepositoryMock;

    @Override
    protected ProjectFinanceRowController supplyControllerUnderTest() {
        return new ProjectFinanceRowController();
    }

    @Test
    public void addShouldCreateNewCost() throws Exception{
        GrantClaimPercentage claim = new GrantClaimPercentage(1L);
        when(validationUtil.validateProjectCostItem(nullable(FinanceRowItem.class))).thenReturn(new ValidationMessages());

        when(projectFinanceRowServiceMock.create(1L, claim)).thenReturn(serviceSuccess(claim));

        mockMvc.perform(post("/project-finance-row")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isCreated());

        verify(projectFinanceRowServiceMock, times(1)).create(1L, claim);
    }

    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(projectFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnBadRequestOnWrongContentType() throws Exception {
        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.IMAGE_GIF))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoMoreInteractions(projectFinanceRowRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {
        when(validationUtil.validateProjectCostItem(isA(FinanceRowItem.class))).thenReturn(new ValidationMessages());
        GrantClaimPercentage costItem = new GrantClaimPercentage(1L);
        when(projectFinanceRowServiceMock.update(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceFailure(notFoundError(FinanceRowMetaField.class, 123L)));

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(projectFinanceRowServiceMock, times(1)).update(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        when(validationUtil.validateProjectCostItem(isA(FinanceRowItem.class))).thenReturn(new ValidationMessages());
        GrantClaimPercentage costItem = new GrantClaimPercentage(1L);
        when(projectFinanceRowServiceMock.update(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));

        mockMvc.perform(put("/project-finance-row/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(projectFinanceRowServiceMock, times(1)).update(eq(123L), isA(FinanceRowItem.class));
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(projectFinanceRowServiceMock.delete(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project-finance-row/123"))
                .andExpect(status().isNoContent());

        verify(projectFinanceRowServiceMock, times(1)).delete(123L);
    }
}
