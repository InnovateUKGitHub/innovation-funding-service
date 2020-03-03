package org.innovateuk.ifs.application.forms.questions.researchcategory.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.researchcategory.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.questions.researchcategory.controller.ResearchCategoryController.APPLICATION_SAVED_MESSAGE;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ResearchCategoryControllerTest extends BaseControllerMockMVCTest<ResearchCategoryController> {

    @Override
    protected ResearchCategoryController supplyControllerUnderTest() {
        return new ResearchCategoryController();
    }

    @Mock
    private ApplicationResearchCategoryModelPopulator researchCategoryModelPopulator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private QuestionService questionService;

    @Mock
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Mock
    private ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator;

    @Test
    public void getResearchCategories() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();
        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId()))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId))
                .andExpect(model().attribute("model", researchCategoryViewModel))
                .andExpect(view().name("application/questions/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_applicableChoiceShouldCallServiceAndRedirectToApplication()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_restServiceErrorShouldResultInErrorOnInnovationAreasPage()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        ResearchCategoryForm researchCategoryForm = new ResearchCategoryForm();
        researchCategoryForm.setResearchCategory(researchCategoryId);

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restFailure(new Error("", HttpStatus.NOT_FOUND)));
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId()))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(model().attribute("model", researchCategoryViewModel))
                .andExpect(view().name("application/questions/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, researchCategoryForm);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_missingChoiceWithoutMarkingAsComplete() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(), null))
                .thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId))
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(), null);
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void submitResearchCategoryChoice_markAsComplete()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();
        ProcessRoleResource processRole = newProcessRoleResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(userRestService.findProcessRole(loggedInUser.getId(), applicationResource.getId())).thenReturn(restSuccess(processRole));
        when(applicationResearchCategoryRestService.setResearchCategoryAndMarkAsComplete(applicationResource.getId(),
                researchCategoryId, processRole.getId())).thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId))
                .param("mark_as_complete", ""))
                .andExpect(redirectedUrl(format("/application/%d/form/question/%d/research-category", applicationResource.getId(), questionId)))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(userRestService, applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(userRestService).findProcessRole(loggedInUser.getId(), applicationResource.getId());
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategoryAndMarkAsComplete(applicationResource.getId(),
                researchCategoryId, processRole.getId());
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_markAsCompleteWithMissingChoiceShouldError()
            throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId()))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("mark_as_complete", ""))
                .andExpect(model().attribute("model", researchCategoryViewModel))
                .andExpect(view().name("application/questions/research-categories"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "researchCategory"));

        InOrder inOrder = inOrder(userRestService, applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markAsIncomplete() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();
        ProcessRoleResource processRole = newProcessRoleResource().build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(userRestService.findProcessRole(loggedInUser.getId(), applicationResource.getId())).thenReturn(restSuccess(processRole));
        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId()))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("mark_as_incomplete", ""))
                .andExpect(model().attribute("model", researchCategoryViewModel))
                .andExpect(view().name("application/questions/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(userRestService, questionService, applicationService,
                researchCategoryModelPopulator, researchCategoryFormPopulator, applicationResearchCategoryRestService);
        inOrder.verify(userRestService).findProcessRole(loggedInUser.getId(), applicationResource.getId());
        inOrder.verify(questionService).markAsIncomplete(questionId, applicationResource.getId(), processRole.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }
}