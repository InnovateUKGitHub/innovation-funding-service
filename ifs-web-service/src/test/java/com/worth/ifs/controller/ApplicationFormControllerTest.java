package com.worth.ifs.controller;

import com.worth.ifs.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class ApplicationFormControllerTest  extends BaseUnitTest{

    @InjectMocks
    private ApplicationFormController applicationFormController;

    @Mock
    UserService userServiceMock;

    @Before
    public void setUp(){
        super.setup();

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationFormController)
                .setViewResolvers(viewResolver())
                .build();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
    }

    @Test
    public void testApplicationForm() throws Exception {


    }

    @Test
    public void testApplicationFormWithOpenSection() throws Exception {

    }
}