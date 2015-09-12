package com.worth.ifs.login.controller;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.login.HomeController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class HomeControllerTest  extends BaseUnitTest {
    @InjectMocks
    private HomeController homeController;
    @Before
    public void setUp(){
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(homeController)
                .setViewResolvers(viewResolver())
                .build();
    }

    /**
     * Test if you are redirected to the login page, when you visit the root url http://<domain>/
     */
    @Test
    public void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
}