package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
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
public class AssessorProfileDeclarationControllerTest extends BaseControllerMockMVCTest<AssessorProfileDeclarationController> {

    @Spy
    @InjectMocks
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Override
    protected AssessorProfileDeclarationController supplyControllerUnderTest() {
        return new AssessorProfileDeclarationController();
    }

    @Test
    public void getDeclaration() throws Exception {
        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("profile/declaration-of-interest"));
    }

    @Test
    public void submitDeclaration() throws Exception {

    }
}