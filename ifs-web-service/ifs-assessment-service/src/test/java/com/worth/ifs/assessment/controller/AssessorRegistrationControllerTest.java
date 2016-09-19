package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorTermsModelPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorRegistrationControllerTest extends BaseControllerMockMVCTest<AssessorRegistrationController> {

    @Spy
    @InjectMocks
    private AssessorSkillsModelPopulator assessorSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorDeclarationModelPopulator assessorDeclarationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorTermsModelPopulator assessorTermsModelPopulator;


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
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void declaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void terms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/terms"));
    }
}