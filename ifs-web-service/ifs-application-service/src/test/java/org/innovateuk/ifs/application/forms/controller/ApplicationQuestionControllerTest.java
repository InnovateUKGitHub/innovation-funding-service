package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationQuestionControllerTest extends BaseControllerMockMVCTest<ApplicationQuestionController> {

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected ApplicationQuestionController supplyControllerUnderTest() {
        return new ApplicationQuestionController();
    }

    @Test
    public void questionPage() throws Exception {
        ApplicationResource application = newApplicationResource().build();
        QuestionResource question = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(questionRestService.findById(question.getId())).thenReturn(restSuccess(question));

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}", application.getId(), question.getId()))
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/generic", application.getId(), question.getId())));

    }
}
