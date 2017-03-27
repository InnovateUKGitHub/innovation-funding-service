package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationInnovationAreaPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.InnovationAreaViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class InnovationAreaControllerTest extends BaseControllerMockMVCTest<InnovationAreaController> {
    @Override
    protected InnovationAreaController supplyControllerUnderTest() {
        return new InnovationAreaController();
    }

    @Mock
    private ApplicationInnovationAreaPopulator applicationInnovationAreaPopulator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Test
    public void getInnovationAreas() throws Exception {
        Long applicationId = 1L;
        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationInnovationAreaPopulator.populate(applicationId, questionId)).thenReturn(innovationAreaViewModel);

        MvcResult result = mockMvc.perform(get(ApplicationFormController.APPLICATION_BASE_URL+"1/form/question/2/innovation-area"))
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

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationInnovationAreaPopulator.populate(applicationId, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.saveApplicationInnovationAreaChoice(applicationId, innovationAreaId)).thenReturn(RestResult.restSuccess(newApplicationResource().build()));


        MvcResult result = mockMvc.perform(post(ApplicationFormController.APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
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
        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationInnovationAreaPopulator.populate(applicationId, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.setApplicationInnovationAreaToNotApplicable(applicationId)).thenReturn(RestResult.restSuccess(newApplicationResource().build()));


        MvcResult result = mockMvc.perform(post(ApplicationFormController.APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
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
        Long questionId = 2L;

        Long nonExistentInnovationAreaId = 3L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        RestResult<ApplicationResource> result = restFailure(new Error("", HttpStatus.NOT_FOUND));

        when(applicationInnovationAreaPopulator.populate(applicationId, questionId)).thenReturn(innovationAreaViewModel);
        when(applicationInnovationAreaRestService.saveApplicationInnovationAreaChoice(applicationId, nonExistentInnovationAreaId)).thenReturn(result);


        MvcResult mvcResult = mockMvc.perform(post(ApplicationFormController.APPLICATION_BASE_URL+"1/form/question/2/innovation-area")
                .param("innovationAreaChoice", nonExistentInnovationAreaId.toString()))
                .andExpect(view().name("application/innovation-areas"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verifyZeroInteractions(cookieFlashMessageFilter);
    }

   /* @Test
    public void submitInnovationAreaChoice_missingChoiceShouldThrowError() throws Exception {
        Long applicationId = 1L;
        Long questionId = 2L;

        InnovationAreaViewModel innovationAreaViewModel = new InnovationAreaViewModel();

        when(applicationInnovationAreaPopulator.populate(applicationId, questionId)).thenReturn(innovationAreaViewModel);

        MvcResult mvcResult = mockMvc.perform(post(ApplicationFormController.APPLICATION_BASE_URL+"1/form/question/2/innovation-area"))
                .andExpect(view().name("application/innovation-areas"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        verify(applicationInnovationAreaPopulator).populate(any(), any());
        verifyZeroInteractions(applicationInnovationAreaRestService);
        verifyZeroInteractions(cookieFlashMessageFilter);
    }*/
}