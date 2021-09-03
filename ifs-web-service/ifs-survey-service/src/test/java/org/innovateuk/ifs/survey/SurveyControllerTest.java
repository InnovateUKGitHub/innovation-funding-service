package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.survey.controller.SurveyController;
import org.innovateuk.ifs.util.NavigationUtils;
import org.innovateuk.ifs.survey.service.SurveyRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SurveyControllerTest extends BaseControllerMockMVCTest<SurveyController> {

    @Mock
    private SurveyRestService surveyRestService;

    @Spy
    @SuppressWarnings("unused")
    private NavigationUtils navigationUtils;

    private String ifsWebBaseURL = "http://localhost:80";

    @Override
    protected SurveyController supplyControllerUnderTest() {
        return new SurveyController();
    }

    @Test
    public void viewFeedback() throws Exception {

        long competitionId = 1L;

        mockMvc.perform(get("/{competitionId}/feedback", competitionId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("survey/survey"));
    }

    @Test
    public void submitFeedbackInvalid() throws Exception {

        long competitionId = 1L;

        SurveyResource surveyResource = new SurveyResource();

        surveyResource.setSurveyType(SurveyType.APPLICATION_SUBMISSION);
        surveyResource.setTargetType(SurveyTargetType.COMPETITION);
        surveyResource.setTargetId(competitionId);
        surveyResource.setSatisfaction(null);
        surveyResource.setComments(null);

        when(surveyRestService.save(surveyResource)).
                thenReturn(restSuccess());

        mockMvc.perform(post("/{competitionId}/feedback", competitionId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("survey/survey"));

    }

    @Test
    public void submitFeedbackValid() throws Exception {

        ReflectionTestUtils.setField(navigationUtils, "ifsWebBaseURL", ifsWebBaseURL);

        long competitionId = 1L;

        SurveyResource surveyResource = new SurveyResource();

        surveyResource.setSurveyType(SurveyType.APPLICATION_SUBMISSION);
        surveyResource.setTargetType(SurveyTargetType.COMPETITION);
        surveyResource.setTargetId(competitionId);
        surveyResource.setSatisfaction(Satisfaction.VERY_DISSATISFIED);
        surveyResource.setComments("comments");

        when(surveyRestService.save(surveyResource)).
                thenReturn(restSuccess());

        mockMvc.perform(post("/{competitionId}/feedback", competitionId)
                .param("comments", "comments")
                .param("satisfaction", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:http://localhost:80"));
    }
}
