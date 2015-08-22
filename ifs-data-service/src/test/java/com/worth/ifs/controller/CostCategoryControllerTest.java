package com.worth.ifs.controller;

import com.worth.ifs.Application;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.domain.Question;
import com.worth.ifs.repository.CostCategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostCategoryControllerTest {

    @Mock
    CostCategoryController costCategoryController;

    @Mock
    CostCategoryRepository costCategoryRepository;

    CostCategory costCategory;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(costCategoryController)
                .build();
        ApplicationFinance applicationFinance = new ApplicationFinance();
        applicationFinance.setId(1L);
        Question question = new Question();
        costCategory = new CostCategory(1L, applicationFinance, question);
    }

    @Test
    public void applicationFinanceControllerShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {
        when(costCategoryRepository.findByApplicationFinanceId(1L)).thenReturn(new ArrayList<CostCategory>()
        {{add(costCategory);}});
        MvcResult mvcResult = mockMvc.perform(get("/costcategory/findByApplicationFinance/1"))
                .andExpect(status().isOk()

                ).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}
