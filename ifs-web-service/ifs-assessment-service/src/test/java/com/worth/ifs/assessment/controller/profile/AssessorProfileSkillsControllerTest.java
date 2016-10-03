package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorRegistrationSkillsModelPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorProfileSkillsControllerTest extends BaseControllerMockMVCTest<AssessorProfileSkillsController> {

    @Spy
    @InjectMocks
    private AssessorRegistrationSkillsModelPopulator assessorRegistrationSkillsModelPopulator;

    @Override
    protected AssessorProfileSkillsController supplyControllerUnderTest() {
        return new AssessorProfileSkillsController();
    }

    @Test
    public void getSkills() throws Exception {
        mockMvc.perform(get("/profile/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void submitSkills() throws Exception {

    }
}