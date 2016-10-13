package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsModelPopulator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorProfileTermsControllerTest extends BaseControllerMockMVCTest<AssessorProfileTermsController> {

    @Spy
    @InjectMocks
    private AssessorProfileTermsModelPopulator assessorProfileTermsModelPopulator;

    @Override
    protected AssessorProfileTermsController supplyControllerUnderTest() {
        return new AssessorProfileTermsController();
    }

    @Test
    public void getTerms() throws Exception {
        mockMvc.perform(get("/profile/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("profile/terms"));
    }

    @Test
    public void submitTerms() throws Exception {

    }
}