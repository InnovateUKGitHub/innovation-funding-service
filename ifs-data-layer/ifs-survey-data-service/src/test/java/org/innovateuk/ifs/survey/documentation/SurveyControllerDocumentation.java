package org.innovateuk.ifs.survey.documentation;

import org.innovateuk.ifs.survey.Satisfaction;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.SurveyTargetType;
import org.innovateuk.ifs.survey.SurveyType;
import org.innovateuk.ifs.survey.controller.SurveyController;
import org.innovateuk.ifs.survey.transactional.SurveyService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.survey.SurveyResourceBuilder.newSurveyResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SurveyControllerDocumentation extends MockMvcTest<SurveyController> {

    @Mock
    private SurveyService surveyService;

    @Override
    public SurveyController supplyControllerUnderTest() {
        return new SurveyController();
    }

    @Test
    public void save() throws Exception {
        SurveyResource surveyResource = newSurveyResource()
                .withSurveyTargetType(SurveyTargetType.COMPETITION)
                .withSurveyType(SurveyType.APPLICATION_SUBMISSION)
                .withTargetId(1L)
                .withSatisfaction(Satisfaction.DISSATISFIED)
                .withComments("Some comments")
                .build();

        when(surveyService.save(surveyResource)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/survey")
                        .content(json(surveyResource))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());
    }

}
