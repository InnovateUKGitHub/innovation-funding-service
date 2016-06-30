package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by David Chen on 29/06/16.
 */
@WebAppConfiguration
public class AssessmentControllerTest {

    protected MockMvc mvc;

    private static final Long ASSESSMENT_ID = 1L;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Mock
    private AssessmentFeedbackService assessmentFeedbackService;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public void testGetAllQuestionsOfGivenAssessment() throws Exception{
        AssessmentFeedbackResource assessmentFeedbackResource = AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource().build();
        List<AssessmentFeedbackResource> listOfAssessmentFeedback = new ArrayList<>();
        listOfAssessmentFeedback.add(assessmentFeedbackResource);


       // String uri = "/assessment/summary/";
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/assessment/summary/{assessmentId}",ASSESSMENT_ID)).andExpect(status().isOk()).andReturn();



    }




}
