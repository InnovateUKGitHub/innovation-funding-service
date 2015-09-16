package com.worth.ifs.finance.controller;

import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.repository.CostValueRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostControllerTest {
    @InjectMocks
    private CostController costController;

    @Mock
    CostRepository costRepositoryMock;

    @Mock
    CostValueRepository costValueRepository;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(costController)
                .build();
    }

    @Test
    public void deleteCostAPICallShouldRenderResponse() throws Exception {
        mockMvc.perform(get("/cost/delete/1"))
                .andExpect(status().isOk());
    }
}