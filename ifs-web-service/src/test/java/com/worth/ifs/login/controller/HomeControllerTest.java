package com.worth.ifs.login.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.login.HomeController;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class HomeControllerTest extends BaseControllerMockMVCTest<HomeController> {

    @Override
    protected HomeController supplyControllerUnderTest() {
        return new HomeController();
    }

    /**
     * Test if you are redirected to the login page, when you visit the root url http://<domain>/
     */
    @Test
    public void testHome() throws Exception {

        setLoggedInUser(null);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }
}