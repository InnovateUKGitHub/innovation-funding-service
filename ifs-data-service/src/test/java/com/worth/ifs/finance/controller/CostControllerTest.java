package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandler;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandlerImpl;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    OrganisationFinanceHandler organisationFinanceHandler = new OrganisationFinanceHandlerImpl();

    @Before
    public void setUp() throws Exception {
        value = new BigDecimal(1000);
        quantity = new Integer(25);
    }

    //@Test
    public void addShouldCreateNewCost() throws Exception{
        ApplicationFinance applicationFinance = new ApplicationFinance();
        Question question = new Question();

        when(applicationFinanceRepository.findOne(123L)).thenReturn(applicationFinance);
        when(questionRepository.findOne(123L)).thenReturn(question);

        mockMvc.perform(get("/cost/add/{applicationFinanceId}/{questionId}", "123", "123"))
                .andExpect(status().isOk());

        verify(costRepositoryMock, times(1)).save(any(Cost.class));
        verifyNoMoreInteractions(costRepositoryMock);
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
        mockMvc.perform(get("/cost/get/{applicationFinanceId}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void updateShouldReturnEmptyResponseOnWrongId() throws Exception {
        when(costRepositoryMock.exists(123L)).thenReturn(false);

        MvcResult response = mockMvc.perform(get("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = response.getResponse().getContentAsString();

        Assert.assertEquals(content, "");
    }

    //@Test
    public void updateShouldReturnIsCorrectOnCorrectValues() throws Exception {
        Cost cost1 = new Cost("item1", "desc1", 2, value, new ApplicationFinance(), new Question());
        Cost cost2 = new Cost("item2", "desc2", 4, value, new ApplicationFinance(), new Question());

        when(costRepositoryMock.exists(123L)).thenReturn(true);
        when(costRepositoryMock.findOne(123L)).thenReturn(cost1);
        ObjectMapper mapper = new ObjectMapper();

        String jsonCost = mapper.writeValueAsString(cost2);

        mockMvc.perform(post("/cost/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCost))
                .andExpect(status().isOk());

    }

    @Test
    public void findByApplicationIdShouldReturnEmptyArrayOnWrongId() throws Exception {
        when(costRepositoryMock.findByApplicationFinanceId(123L)).thenReturn(emptyList());

        mockMvc.perform(get("/cost/get/{applicationFinanceId}", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(costRepositoryMock, times(1)).findByApplicationFinanceId(123L);
        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void findByApplicationIdShouldReturnNotFoundOnEmptyId() throws Exception {
       mockMvc.perform(get("/cost/get/{applicationFinanceId}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void findOneCostAPICallShouldReturnCostListOnKnownId() throws Exception {
        ApplicationFinance f = new ApplicationFinance(1L, new Application(), new Organisation());

        Cost c1 = new Cost(1L, "item1", "desc1", 1, value, f, new Question());
        Cost c2 = new Cost(2L, "item2", "desc2", 1, value, f, new Question());
        Cost c3 = new Cost(3L, "item3", "desc3", 1, value, f, new Question());

        when(costRepositoryMock.findByApplicationFinanceId(1L)).thenReturn(Arrays.asList(c1, c2, c3));

        mockMvc.perform(get("/cost/get/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item", is("item1")))
                .andExpect(jsonPath("$[1].item", is("item2")))
                .andExpect(jsonPath("$[2].item", is("item3")));

        verify(costRepositoryMock, times(1)).findByApplicationFinanceId(1L);
        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void findOneCostAPICallShouldReturnNothingOnWrongId() throws Exception {
        when(costRepositoryMock.findOne(123L)).thenReturn(null);

        mockMvc.perform(get("/cost/findById/{id}", "123"))
                .andExpect(status().isOk());

        verify(costRepositoryMock, times(1)).findOne(123L);
        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void findOneCostAPICallShouldReturnNotFoundOnEmptyId() throws Exception {
        mockMvc.perform(get("/cost/findById/{id}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void findOneCostAPICallShouldReturnCostOnKnownId() throws Exception {
        FormInputType formInputType = new FormInputType(5L, "materials");
        FormInput formInput = newFormInput().build();
        formInput.setFormInputType(formInputType);
        Question question = newQuestion().withFormInputs(Arrays.asList(formInput)).build();
        Cost cost = new Cost(1L, "item", "", 1, value, new ApplicationFinance(), question);
        when(costRepositoryMock.findOne(1L)).thenReturn(cost);
        CostItem costItem = new Materials(1L, "item", value, quantity);
        when(organisationFinanceHandler.costToCostItem(cost)).thenReturn(costItem);

        mockMvc.perform(get("/cost/findById/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item", is("item")))
                .andExpect(jsonPath("$.cost", is(value.intValue())))
                .andExpect(jsonPath("$.quantity", is(quantity.intValue())))
                .andExpect(jsonPath("$.total", is(value.multiply(BigDecimal.valueOf(quantity)).intValue())));

        verify(costRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(costRepositoryMock);
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {
        mockMvc.perform(get("/cost/delete/1"))
                .andExpect(status().isOk());
    }

}