package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class AssessorRegistrationControllerTest extends BaseControllerMockMVCTest<AssessorRegistrationController> {

    @Override
    protected AssessorRegistrationController supplyControllerUnderTest() {
        return new AssessorRegistrationController();
    }

    @Test
    public void register() throws Exception {
        mockMvc.perform(get("/registration/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"));
    }
}