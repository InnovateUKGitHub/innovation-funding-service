package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationAjaxControllerTest extends AbstractApplicationMockMVCTest<ApplicationAjaxController> {


    private ApplicationResource application;
    private Long questionId;
    private Long formInputId;
    private Long costId;

    @Override
    protected ApplicationAjaxController supplyControllerUnderTest() {
        return new ApplicationAjaxController();
    }

    @Before
    public void setUpData() {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();

        application = applications.get(0);
        questionId = 1L;
        formInputId = 111L;
        costId = 1L;

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyLong(), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyLong(), anyBoolean())).thenReturn(restSuccess(noErrors()));
    }

    @Test
    public void testSaveFormElement() throws Exception {
        String value = "Form Input " + formInputId + " Response";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", formInputId.toString())
                        .param("fieldName", "formInput[" + formInputId + "]")
                        .param("value", value)
                        .param("multipleChoiceOptionId", "")
        ).andExpect(status().isOk());

        Mockito.inOrder(formInputResponseRestService).verify(formInputResponseRestService, calls(1)).saveQuestionResponse(loggedInUser.getId(), application.getId(), formInputId, value, null, false);
    }

    @Test
    public void testSaveFormElementMultipleChoiceOption() throws Exception {
        String value = "Form Input " + formInputId + " Response";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", formInputId.toString())
                        .param("fieldName", "formInput[" + formInputId + "]")
                        .param("value", "")
                        .param("multipleChoiceOptionId", "1")
        ).andExpect(status().isOk());

        Mockito.inOrder(formInputResponseRestService).verify(formInputResponseRestService, calls(1)).saveQuestionResponse(loggedInUser.getId(), application.getId(), formInputId, "", 1L, false);
    }
}
