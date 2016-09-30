package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorRegistrationDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationTermsModelPopulator;
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
public class AssessorRegistrationProfileControllerTest  extends BaseControllerMockMVCTest<AssessorRegistrationProfileController> {

    @Spy
    @InjectMocks
    private AssessorRegistrationSkillsModelPopulator assessorRegistrationSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationDeclarationModelPopulator assessorRegistrationDeclarationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationTermsModelPopulator assessorRegistrationTermsModelPopulator;

    @Override
    protected AssessorRegistrationProfileController supplyControllerUnderTest() {
        return new AssessorRegistrationProfileController();
    }

    @Test
    public void profileSkills() throws Exception {
        mockMvc.perform(get("/registration/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void profileDeclaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void profileTerms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/terms"));
    }
}
