package org.innovateuk.ifs.application.overheads;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.areas.controller.InnovationAreaController;
import org.innovateuk.ifs.application.areas.populator.ApplicationInnovationAreaPopulator;
import org.innovateuk.ifs.application.areas.viewmodel.InnovationAreaViewModel;
import org.innovateuk.ifs.application.forms.validator.ApplicationDetailsEditableValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InnovationAreaControllerTest extends BaseControllerMockMVCTest<InnovationAreaController> {
    @Override
    protected InnovationAreaController supplyControllerUnderTest() {
        return new InnovationAreaController();
    }

    @Mock
    private ApplicationInnovationAreaPopulator applicationInnovationAreaPopulator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private ApplicationDetailsEditableValidator applicationDetailsEditableValidator;

    @Test
    public void getInnovationAreas() throws Exception {
        Long applicationId = 1L;
        Long questionId = 2L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(true);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        when(applicationInnovationAreaPopulator.populate(applicationResource, questionId)).thenReturn(innovationAreaViewModel);

        MvcResult result = mockMvc.perform(get(APPLICATION_BASE_URL+"1/form/question/2/innovation-area"))
                .andExpect(view().name("application/innovation-areas"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
    }

    @Test
    public void submitInnovationAreaChoice_applicableChoiceShouldCallServiceAndRedirectToApplicationDetails() throws Exception {
        Long applicationId = 1L;
        Long questionId = 2L;
        Long innovationAreaId = 3L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(true);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        when(applicationInnovationAreaPopulator.populate(applicationResource, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.saveApplicationInnovationAreaChoice(applicationId, innovationAreaId)).thenReturn(restSuccess(newApplicationResource().build()));


        MvcResult result = mockMvc.perform(post(APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
        .param("innovationAreaChoice", innovationAreaId.toString()))
                .andExpect(view().name("redirect:/application/1/form/question/2"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verify(cookieFlashMessageFilter).setFlashMessage(any(),any());
        verify(applicationInnovationAreaRestService).saveApplicationInnovationAreaChoice(applicationId, innovationAreaId);
    }

    @Test
    public void submitInnovationAreaChoice_choiceNotApplicableShouldCallServiceAndRedirectToApplicationDetails() throws Exception {
        Long applicationId = 1L;
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(true);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        when(applicationInnovationAreaPopulator.populate(applicationResource, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.setApplicationInnovationAreaToNotApplicable(applicationId)).thenReturn(restSuccess(newApplicationResource().build()));


        MvcResult result = mockMvc.perform(post(APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
                .param("innovationAreaChoice", "NOT_APPLICABLE"))
                .andExpect(view().name("redirect:/application/1/form/question/2"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verify(cookieFlashMessageFilter).setFlashMessage(any(),any());
        verify(applicationInnovationAreaRestService).setApplicationInnovationAreaToNotApplicable(applicationId);
}

    @Test
    public void submitInnovationAreaChoice_restServiceErrorShouldResultInErrorOnInnovationAreasPage() throws Exception {
        Long applicationId = 1L;
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        Long questionId = 2L;

        Long nonExistentInnovationAreaId = 3L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        RestResult<ApplicationResource> result = restFailure(new Error("", HttpStatus.NOT_FOUND));

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(true);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        when(applicationInnovationAreaPopulator.populate(applicationResource, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.saveApplicationInnovationAreaChoice(applicationId, nonExistentInnovationAreaId)).thenReturn(result);


        MvcResult mvcResult = mockMvc.perform(post(APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
                .param("innovationAreaChoice", nonExistentInnovationAreaId.toString()))
                .andExpect(view().name("application/innovation-areas"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verifyZeroInteractions(cookieFlashMessageFilter);
    }

   @Test
    public void submitInnovationAreaChoice_missingChoiceShouldThrowError() throws Exception {
        Long applicationId = 1L;
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(true);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        when(applicationInnovationAreaPopulator.populate(applicationResource, questionId)).thenReturn(innovationAreaViewModel);

        MvcResult mvcResult = mockMvc.perform(post(APPLICATION_BASE_URL+"1/form/question/2/innovation-area"))
                .andExpect(view().name("application/innovation-areas"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "innovationAreaChoice"))
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verifyZeroInteractions(applicationInnovationAreaRestService);
        verifyZeroInteractions(cookieFlashMessageFilter);
    }

    @Test
    public void submitInnovationAreaChoice_validatorReturnFalseShouldResultInForbiddenView() throws Exception {
        Long applicationId = 1L;
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)).thenReturn(false);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());

        MvcResult mvcResult = mockMvc.perform(post(APPLICATION_BASE_URL+"1/form/question/2/innovation-area"))
                .andExpect(view().name("forbidden"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        verifyZeroInteractions(applicationInnovationAreaPopulator);
        verifyZeroInteractions(applicationInnovationAreaRestService);
        verifyZeroInteractions(cookieFlashMessageFilter);
    }
}