package org.innovateuk.ifs.survey.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.survey.Satisfaction;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.SurveyTargetType;
import org.innovateuk.ifs.survey.SurveyType;
import org.innovateuk.ifs.survey.domain.Survey;
import org.innovateuk.ifs.survey.repository.SurveyRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.survey.builder.EuGrantResourceBuilder.newSurveyResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SurveyServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Before
    public void cleanRepository() {
        surveyRepository.deleteAll();
    }

    @Test
    public void save() {
        SurveyResource surveyResource = newSurveyResource()
                .withSurveyTargetType(SurveyTargetType.COMPETITION)
                .withSurveyType(SurveyType.APPLICATION_SUBMISSION)
                .withTargetId(1L)
                .withSatisfaction(Satisfaction.DISSATISFIED)
                .withComments("Some comments")
                .build();


        ServiceResult<Void> result = surveyService.save(surveyResource);

        assertTrue(result.isSuccess());

        List<Survey> surveys = newArrayList(surveyRepository.findAll());

        assertEquals(surveys.size(), 1);

        Survey survey = surveys.get(0);

        assertEquals(survey.getComments(), surveyResource.getComments());
        assertEquals(survey.getTargetType(), surveyResource.getTargetType());
        assertEquals(survey.getTargetId(), surveyResource.getTargetId());
        assertEquals(survey.getSatisfaction(), surveyResource.getSatisfaction());
        assertEquals(survey.getSurveyType(), surveyResource.getSurveyType());

    }

}
