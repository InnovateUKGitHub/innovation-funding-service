package org.innovateuk.ifs.survey.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.transactional.SurveyService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.survey.builder.SurveyResourceBuilder.newSurveyResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SurveyControllerTest extends MockMvcTest<SurveyController> {

    @Mock
    private SurveyService surveyService;

    @Override
    public SurveyController supplyControllerUnderTest() {
        return new SurveyController();
    }

    @Test
    public void save() throws Exception {
        SurveyResource surveyResource = newSurveyResource().build();

        when(surveyService.save(surveyResource)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/survey")
                        .content(json(surveyResource))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(surveyService).save(surveyResource);
    }
}
