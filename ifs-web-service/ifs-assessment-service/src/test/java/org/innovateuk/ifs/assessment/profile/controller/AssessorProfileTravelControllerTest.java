package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.profile.controller.AssessorProfileTravelController;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AssessorProfileTravelControllerTest extends BaseControllerMockMVCTest<AssessorProfileTravelController>  {

    @Override
    protected AssessorProfileTravelController supplyControllerUnderTest() {
        return new AssessorProfileTravelController();
    }

    @Test
    public void getTravelAndSubsistence() throws Exception {

        mockMvc.perform(get("/profile/travel"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/travel"));
    }
}
