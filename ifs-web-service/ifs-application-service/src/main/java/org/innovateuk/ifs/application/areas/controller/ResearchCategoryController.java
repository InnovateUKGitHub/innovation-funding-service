package org.innovateuk.ifs.application.areas.controller;

import org.innovateuk.ifs.application.areas.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.forms.validator.ApplicationDetailsEditableValidator;
import org.innovateuk.ifs.application.forms.validator.QuestionEditableValidator;
import org.innovateuk.ifs.application.forms.validator.ResearchCategoryEditableValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.isMarkQuestionAsCompleteRequest;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller handles requests by Applicants to change the research category choice for an Application.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ResearchCategoryController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ResearchCategoryController {
    static String APPLICATION_SAVED_MESSAGE = "applicationSaved";

    @Autowired
    private ApplicationResearchCategoryModelPopulator researchCategoryModelPopulator;

    @Autowired
    private ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator;

    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private ApplicationDetailsEditableValidator applicationDetailsEditableValidator;

    @Autowired
    private ResearchCategoryEditableValidator researchCategoryEditableValidator;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping("/research-category")
    public String getResearchCategories(Model model,
                                        UserResource loggedInUser,
                                        @ModelAttribute(FORM_ATTR_NAME) ResearchCategoryForm researchCategoryForm,
                                        @PathVariable long applicationId,
                                        @PathVariable long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        model.addAttribute("researchCategoryModel", researchCategoryModelPopulator.populate(
                applicationResource, loggedInUser.getId(), questionId, applicationResource.isUseNewApplicantMenu()));
        researchCategoryFormPopulator.populate(applicationResource, researchCategoryForm);

        return "application/research-categories";
    }

    @PostMapping(params = {"mark_as_incomplete"})
    public String markAsIncomplete(Model model,
                                   UserResource loggedInUser,
                                   @ModelAttribute(FORM_ATTR_NAME) ResearchCategoryForm researchCategoryForm,
                                   @PathVariable long applicationId,
                                   @PathVariable long questionId) {
        questionService.markAsIncomplete(questionId, applicationId, getProcessRoleId(loggedInUser.getId(),
                applicationId));
        return getResearchCategories(model, loggedInUser, researchCategoryForm, applicationId, questionId);
    }

    @PostMapping(params = {"save-research-category"})
    public String submitResearchCategoryChoice(@ModelAttribute(FORM_ATTR_NAME) @Valid ResearchCategoryForm
                                                       researchCategoryForm,
                                               @SuppressWarnings("unused") BindingResult bindingResult,
                                               ValidationHandler validationHandler,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               Model model,
                                               UserResource loggedInUser,
                                               @PathVariable long applicationId,
                                               @PathVariable long questionId) {
        Supplier<String> failureView = () -> getResearchCategories(model, loggedInUser, researchCategoryForm,
                applicationId, questionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ApplicationResource applicationResource = applicationService.getById(applicationId);

            checkIfAllowed(questionId, applicationResource);

            ServiceResult<ApplicationResource> updateResult = saveResearchCategoryChoice(applicationId,
                    loggedInUser.getId(), researchCategoryForm, request);

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                        cookieFlashMessageFilter.setFlashMessage(response, APPLICATION_SAVED_MESSAGE);
                        return getRedirectUrl(applicationResource, questionId);
                    });
        });
    }

    private ServiceResult<ApplicationResource> saveResearchCategoryChoice(long applicationId,
                                                                          long loggedInUserId,
                                                                          ResearchCategoryForm researchCategoryForm,
                                                                          HttpServletRequest request) {
        boolean isMarkQuestionAsCompleteRequest = isMarkQuestionAsCompleteRequest(request.getParameterMap());
        long researchCategory = researchCategoryForm.getResearchCategory();

        RestResult<ApplicationResource> result = isMarkQuestionAsCompleteRequest ?
                applicationResearchCategoryRestService.setResearchCategoryAndMarkAsComplete(applicationId,
                        getProcessRoleId(loggedInUserId, applicationId), researchCategory) :
                applicationResearchCategoryRestService.setResearchCategory(applicationId,
                        researchCategory);

        return result.toServiceResult();
    }

    private void checkIfAllowed(long questionId, ApplicationResource applicationResource)
            throws ForbiddenActionException {
        if (applicationResource.isUseNewApplicantMenu()) {
            checkIfAllowed(questionId, applicationResource, researchCategoryEditableValidator);
        } else {
            checkIfAllowed(questionId, applicationResource, applicationDetailsEditableValidator);
        }
    }

    private void checkIfAllowed(long questionId, ApplicationResource applicationResource,
                                QuestionEditableValidator questionEditableValidator) throws ForbiddenActionException {
        if (!questionEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)) {
            throw new ForbiddenActionException();
        }
    }

    private long getProcessRoleId(long userId, long applicationId) {
        return processRoleService.findProcessRole(userId, applicationId).getId();
    }

    private String getRedirectUrl(ApplicationResource applicationResource, long questionId) {
        if (applicationResource.isUseNewApplicantMenu()) {
            return "redirect:" + APPLICATION_BASE_URL + applicationResource.getId();
        } else {
            return "redirect:" + APPLICATION_BASE_URL + applicationResource.getId() + "/form/question/" + questionId;
        }
    }
}
