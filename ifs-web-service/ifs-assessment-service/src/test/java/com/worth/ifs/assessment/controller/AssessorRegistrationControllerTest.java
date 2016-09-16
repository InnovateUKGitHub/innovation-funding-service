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

    @Test
    public void skills() throws Exception {
        mockMvc.perform(get("/registration/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void declaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void terms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/terms"));
    }
}