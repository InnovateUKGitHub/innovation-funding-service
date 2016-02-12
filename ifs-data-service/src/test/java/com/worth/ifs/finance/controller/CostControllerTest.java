package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.finance.transactional.CostService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.finance.builder.CostBuilder.newCost;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Ignore("TODO DW - INFUND-1555 - reinstate test")
public class CostControllerTest extends BaseControllerMockMVCTest<CostController> {

    private BigDecimal value;
    private Integer quantity;

    @Override
    protected CostController supplyControllerUnderTest() {
        return new CostController();
    }

    @Mock
    private CostService costServiceMock;

    @Before
    public void setUp() throws Exception {
        value = new BigDecimal(1000);
        quantity = new Integer(25);
    }

    @Test
    public void addShouldCreateNewCost() throws Exception{

        when(costServiceMock.addCost(123L, 456L, null)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/add/{applicationFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isOk());

        verify(costServiceMock, times(1)).addCost(123L, 456L, null);
    }

    @Test
    public void updateShouldReturnBadRequestOnMissingBody() throws Exception {
        mockMvc.perform(put("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(costRepositoryMock);
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

        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void updateShouldReturnNotFoundOnEmptyId() throws Exception {

        when(costServiceMock.getCostFieldById(null)).thenReturn(serviceFailure(notFoundError(CostField.class, 1L)));

        mockMvc.perform(get("/cost/get/{applicationFinanceId}", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(costServiceMock.updateCost(eq(123L), isA(CostItem.class))).thenReturn(serviceFailure(notFoundError(CostField.class, 123L)));

        mockMvc.perform(get("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(costServiceMock, times(1)).updateCost(eq(123L), isA(CostItem.class));
    }

    @Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(costServiceMock.updateCost(eq(123L), isA(CostItem.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk());

        verify(costServiceMock, times(1)).updateCost(eq(123L), isA(CostItem.class));
    }

    @Test
    public void findByApplicationId() throws Exception {

        List<Cost> costsToGet = newCost().build(2);
        when(costServiceMock.findCostsByApplicationId(123L)).thenReturn(serviceSuccess(costsToGet));

        MvcResult mvcResult = mockMvc.perform(get("/cost/get/{applicationFinanceId}", "123"))
                .andExpect(status().isOk()).andReturn();

        Cost[] costs = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Cost[].class);
        assertEquals(2, costs.length);
        assertEquals(costsToGet.get(0).getId(), costs[0].getId());
        assertEquals(costsToGet.get(1).getId(), costs[1].getId());

        verify(costServiceMock, times(1)).findCostsByApplicationId(123L);
    }

    @Test
    public void findOneCostAPICallShouldReturnNotFoundOnWrongId() throws Exception {

        when(costServiceMock.findCostById(123L)).thenReturn(serviceFailure(notFoundError(Cost.class, 123L)));

        mockMvc.perform(get("/cost/findById/{id}", "123"))
                .andExpect(status().isNotFound());

        verify(costServiceMock, times(1)).findCostById(123L);
    }

    @Test
    public void findOneCostAPICallShouldReturnCostOnKnownId() throws Exception {

        Cost foundCost = newCost().build();
        when(costServiceMock.findCostById(123L)).thenReturn(serviceSuccess(foundCost));

        MvcResult mvcResult = mockMvc.perform(get("/cost/findById/{id}", "123"))
                .andExpect(status().isOk())
                .andReturn();

        Cost returnedCost = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Cost.class);
        assertEquals(foundCost.getId(), returnedCost.getId());

        verify(costServiceMock, times(1)).findCostById(123L);
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {

        when(costServiceMock.deleteCost(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/cost/delete/123"))
                .andExpect(status().isOk());

        verify(costServiceMock, times(1)).deleteCost(123L);
    }

}