package org.innovateuk.ifs.content;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class StaticContentControllerTest extends BaseControllerMockMVCTest<StaticContentController> {


    @Override
    protected StaticContentController supplyControllerUnderTest() {
        return new StaticContentController();
    }

    @Test
    public void contact() throws Exception {
        mockMvc.perform(get("/info/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("content/contact"));
    }

    @Test
    public void cookies() throws Exception {
        mockMvc.perform(get("/info/cookies"))
                .andExpect(status().isOk())
                .andExpect(view().name("content/cookies"));
    }

    @Test
    public void termsAndConditions() throws Exception {
        mockMvc.perform(get("/info/terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(view().name("content/terms-and-conditions"));
    }
}