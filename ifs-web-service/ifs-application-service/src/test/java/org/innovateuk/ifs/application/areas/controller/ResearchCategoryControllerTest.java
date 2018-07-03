package org.innovateuk.ifs.application.areas.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.areas.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.forms.validator.ApplicationDetailsEditableValidator;
import org.innovateuk.ifs.application.forms.validator.ResearchCategoryEditableValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationInnovationAreaRestService;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.areas.controller.ResearchCategoryController.APPLICATION_SAVED_MESSAGE;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
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
    private ApplicationDetailsEditableValidator applicationDetailsEditableValidator;

    @Mock
    private ResearchCategoryEditableValidator researchCategoryEditableValidator;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private QuestionService questionService;

    @Mock
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Mock
    private ApplicationInnovationAreaRestService applicationInnovationAreaRestService;

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
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), false))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationService, applicationDetailsEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, false);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_applicableChoiceShouldCallServiceAndRedirectToApplicationDetails()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(redirectedUrl(format("/application/%s/form/question/%s", applicationResource.getId(),
                        questionId)))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(applicationService, applicationDetailsEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_restServiceErrorShouldResultInErrorOnInnovationAreasPage() throws
            Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
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
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restFailure(new Error("", HttpStatus.NOT_FOUND)));
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), false))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationService, applicationDetailsEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, false);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, researchCategoryForm);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_missingChoiceShouldThrowError() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), false))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "researchCategory"));

        InOrder inOrder = inOrder(applicationService, applicationDetailsEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, false);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_validatorReturnFalseShouldResultInForbiddenView() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(false);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", "1"))
                .andExpect(view().name("forbidden"))
                .andExpect(status().is4xxClientError());

        InOrder inOrder = inOrder(applicationService, applicationDetailsEditableValidator);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verifyNoMoreInteractions();
        verifyZeroInteractions(applicationResearchCategoryRestService, researchCategoryModelPopulator,
                researchCategoryFormPopulator, cookieFlashMessageFilter);
    }

    @Test
    public void submitResearchCategoryChoice_newApplicantMenu_applicableChoiceShouldCallServiceAndRedirectToApplication()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource()
                .withUseNewApplicantMenu(true)
                .build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(applicationService, researchCategoryEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_newApplicantMenu_restServiceErrorShouldResultInErrorOnInnovationAreasPage()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource()
                .withUseNewApplicantMenu(true)
                .build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                true,
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
        when(researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(applicationResearchCategoryRestService.setResearchCategory(applicationResource.getId(),
                researchCategoryId)).thenReturn(restFailure(new Error("", HttpStatus.NOT_FOUND)));
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), true))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId)))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationService, researchCategoryEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, true);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, researchCategoryForm);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_newApplicantMenu_missingChoiceShouldThrowError() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource()
                .withUseNewApplicantMenu(true)
                .build();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel(
                "test competition",
                applicationResource.getId(),
                questionId,
                newResearchCategoryResource().build(2),
                false,
                true,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), true))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "researchCategory"));

        InOrder inOrder = inOrder(applicationService, researchCategoryEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, true);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitResearchCategoryChoice_newApplicantMenu_validatorReturnFalseShouldResultInForbiddenView() throws Exception {
        long questionId = 1L;

        ApplicationResource applicationResource = newApplicationResource()
                .withUseNewApplicantMenu(true)
                .build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(false);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", "1"))
                .andExpect(view().name("forbidden"))
                .andExpect(status().is4xxClientError());

        InOrder inOrder = inOrder(applicationService, researchCategoryEditableValidator);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verifyNoMoreInteractions();
        verifyZeroInteractions(applicationResearchCategoryRestService, researchCategoryModelPopulator,
                researchCategoryFormPopulator, cookieFlashMessageFilter);
    }

    @Test
    public void submitResearchCategoryChoice_newApplicantMenu_markAsComplete()
            throws Exception {
        long questionId = 1L;
        long researchCategoryId = 2L;

        ApplicationResource applicationResource = newApplicationResource()
                .withUseNewApplicantMenu(true)
                .build();
        ProcessRoleResource processRole = newProcessRoleResource().build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(processRoleService.findProcessRole(loggedInUser.getId(), applicationResource.getId())).thenReturn(processRole);
        when(applicationResearchCategoryRestService.setResearchCategoryAndMarkAsComplete(applicationResource.getId(),
                researchCategoryId, processRole.getId())).thenReturn(restSuccess(newApplicationResource().build()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("researchCategory", String.valueOf(researchCategoryId))
                .param("mark_as_complete", ""))
                .andExpect(redirectedUrl(format("/application/%s", applicationResource.getId())))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(processRoleService, applicationService, researchCategoryEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, cookieFlashMessageFilter,
                applicationResearchCategoryRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(researchCategoryEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(processRoleService).findProcessRole(loggedInUser.getId(), applicationResource.getId());
        inOrder.verify(applicationResearchCategoryRestService).setResearchCategoryAndMarkAsComplete(applicationResource.getId(),
                researchCategoryId, processRole.getId());
        inOrder.verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class),
                same(APPLICATION_SAVED_MESSAGE));
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
                false,
                "Industrial research",
                false,
                false,
                false,
                false,
                false,
                "Steve Smith");

        when(processRoleService.findProcessRole(loggedInUser.getId(), applicationResource.getId())).thenReturn(processRole);
        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)).thenReturn(true);
        when(researchCategoryModelPopulator.populate(applicationResource, questionId, loggedInUser.getId(), false))
                .thenReturn(researchCategoryViewModel);

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category",
                applicationResource.getId(), questionId)
                .param("mark_as_incomplete", ""))
                .andExpect(model().attribute("researchCategoryModel", researchCategoryViewModel))
                .andExpect(view().name("application/research-categories"))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(processRoleService, questionService, applicationService, applicationDetailsEditableValidator,
                researchCategoryModelPopulator, researchCategoryFormPopulator, applicationResearchCategoryRestService);
        inOrder.verify(processRoleService).findProcessRole(loggedInUser.getId(), applicationResource.getId());
        inOrder.verify(questionService).markAsIncomplete(questionId, applicationResource.getId(), processRole.getId());
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationDetailsEditableValidator).questionAndApplicationHaveAllowedState(questionId,
                applicationResource);
        inOrder.verify(researchCategoryModelPopulator).populate(applicationResource, loggedInUser.getId(),
                questionId, false);
        inOrder.verify(researchCategoryFormPopulator).populate(applicationResource, new ResearchCategoryForm());
        inOrder.verifyNoMoreInteractions();
    }
}