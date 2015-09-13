package com.worth.ifs.dashboard.controller;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.dashboard.ApplicantController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
//@ContextConfiguration
public class ApplicantControllerTest extends BaseUnitTest {


    @InjectMocks
    private ApplicantController applicantController;



    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicantController)
                .setViewResolvers(viewResolver())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
    }


    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDashboard() throws Exception {


        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(0)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)));

    }
}