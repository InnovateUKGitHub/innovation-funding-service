package org.innovateuk.ifs.survey;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IntegrationTestConfig.class)
//@Ignore("Integration test for SurveyRestServiceImpl and TokenRestTemplateAdaptor, requires docker survey data-service.")
public class SurveyIntegrationTest {

    @Autowired
    private SurveyRestService surveyRestService;

    @Test
    public void test() {
        SurveyResource surveyResource = new SurveyResource();

        surveyResource.setComments("Some comments");
        surveyResource.setSurveyType(SurveyType.APPLICATION_SUBMISSION);
        surveyResource.setSatisfaction(Satisfaction.VERY_DISSATISFIED);
        surveyResource.setTargetType(SurveyTargetType.COMPETITION);
        surveyResource.setTargetId(10L);

        RestResult<Void> result = surveyRestService.save(surveyResource);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }
}
