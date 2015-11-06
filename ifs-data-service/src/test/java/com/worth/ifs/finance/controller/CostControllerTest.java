package com.worth.ifs.finance.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.repository.CostValueRepository;
import com.worth.ifs.user.domain.Organisation;
import junit.framework.Assert;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostControllerTest {

    @Mock
    ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    CostRepository costRepository;

    @Mock
    CostFieldRepository costFieldRepository;

    @Mock
    CostValueRepository costValueRepository;

    private MockMvc mockMvc;

    @InjectMocks
    private CostController costController;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(costController)
                .build();
    }

    

    @Test
    public void updateShouldReturnNotFoundOnEmptyId() throws Exception {
        mockMvc.perform(get("/cost/get/{applicationFinanceId}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findByApplicationIdShouldReturnEmptyArrayOnWrongId() throws Exception {
        when(costRepository.findByApplicationFinanceId(123L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/cost/get/{applicationFinanceId}", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(costRepository, times(1)).findByApplicationFinanceId(123L);
        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findByApplicationIdShouldReturnNotFoundOnEmptyId() throws Exception {
       mockMvc.perform(get("/cost/get/{applicationFinanceId}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findOneCostAPICallShouldReturnCostListOnKnownId() throws Exception {
        ApplicationFinance f = new ApplicationFinance(1L, new Application(), new Organisation());

        Cost c1 = new Cost(1L, "item1", "desc1", 1, 1.1, f, new Question());
        Cost c2 = new Cost(2L, "item2", "desc2", 1, 1.1, f, new Question());
        Cost c3 = new Cost(3L, "item3", "desc3", 1, 1.1, f, new Question());

        when(costRepository.findByApplicationFinanceId(1L)).thenReturn(Arrays.asList(c1, c2, c3));

        mockMvc.perform(get("/cost/get/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item", is("item1")))
                .andExpect(jsonPath("$[1].item", is("item2")))
                .andExpect(jsonPath("$[2].item", is("item3")));

        verify(costRepository, times(1)).findByApplicationFinanceId(1L);
        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findOneCostAPICallShouldReturnNothingOnWrongId() throws Exception {
        when(costRepository.findOne(123L)).thenReturn(null);

        mockMvc.perform(get("/cost/findById/{id}", "123"))
                .andExpect(status().isOk());

        verify(costRepository, times(1)).findOne(123L);
        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findOneCostAPICallShouldReturnNotFoundOnEmptyId() throws Exception {
        mockMvc.perform(get("/cost/findById/{id}", ""))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void findOneCostAPICallShouldReturnCostOnKnownId() throws Exception {
        when(costRepository.findOne(1L)).thenReturn(new Cost(1L, "item", "desc", 1, 1.1,
                new ApplicationFinance(), new Question()));

        mockMvc.perform(get("/cost/findById/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item", is("item")))
                .andExpect(jsonPath("$.description", is("desc")));

        verify(costRepository, times(1)).findOne(1L);
        verifyNoMoreInteractions(costRepository);
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {
        mockMvc.perform(get("/cost/delete/1"))
                .andExpect(status().isOk());
    }

}