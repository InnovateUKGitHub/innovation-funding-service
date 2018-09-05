package org.innovateuk.ifs.application.forms.researchcategory.controller;

import org.innovateuk.ifs.application.forms.researchcategory.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.forms.researchcategory.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.forms.researchcategory.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.forms.validator.ResearchCategoryEditableValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.ValidationUtil;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.isMarkQuestionAsCompleteRequest;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller handles requests by Applicants to change the research category choice for an Application.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ResearchCategoryController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ResearchCategoryController {
    static final String APPLICATION_SAVED_MESSAGE = "applicationSaved";

    @Autowired
    private ApplicationResearchCategoryModelPopulator researchCategoryModelPopulator;

    @Autowired
    private ApplicationResearchCategoryFormPopulator researchCategoryFormPopulator;

    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private ResearchCategoryEditableValidator researchCategoryEditableValidator;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private Validator validator;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getResearchCategories(Model model,
                                        UserResource loggedInUser,
                                        @ModelAttribute(FORM_ATTR_NAME) ResearchCategoryForm researchCategoryForm,
                                        @PathVariable long applicationId,
                                        @PathVariable long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        model.addAttribute("researchCategoryModel", researchCategoryModelPopulator.populate(
                applicationResource, loggedInUser.getId(), questionId));
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

    @PostMapping
    public String submitResearchCategoryChoice(@ModelAttribute(FORM_ATTR_NAME) ResearchCategoryForm
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

        boolean markQuestionAsCompleteRequest = isMarkQuestionAsCompleteRequest(request.getParameterMap());

        // Allow invalid form if not marking as complete
        if (markQuestionAsCompleteRequest) {
            ValidationUtil.isValid(bindingResult, researchCategoryForm, Default.class);
        }

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ApplicationResource applicationResource = applicationService.getById(applicationId);

            checkIfAllowed(questionId, applicationResource);

            ServiceResult<ApplicationResource> updateResult = saveResearchCategoryChoice(applicationId,
                    loggedInUser.getId(), researchCategoryForm, markQuestionAsCompleteRequest);

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                        cookieFlashMessageFilter.setFlashMessage(response, APPLICATION_SAVED_MESSAGE);
                        return getRedirectUrl(applicationResource);
                    });
        });
    }

    private ServiceResult<ApplicationResource> saveResearchCategoryChoice(long applicationId,
                                                                          long loggedInUserId,
                                                                          ResearchCategoryForm researchCategoryForm,
                                                                          boolean markQuestionAsCompleteRequest) {
        Long researchCategory = researchCategoryForm.getResearchCategory();

        RestResult<ApplicationResource> result = markQuestionAsCompleteRequest ?
                applicationResearchCategoryRestService.setResearchCategoryAndMarkAsComplete(applicationId,
                        getProcessRoleId(loggedInUserId, applicationId), researchCategory) :
                applicationResearchCategoryRestService.setResearchCategory(applicationId,
                        researchCategory);

        return result.toServiceResult();
    }

    private void checkIfAllowed(long questionId, ApplicationResource applicationResource)
            throws ForbiddenActionException {
        if (!researchCategoryEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)) {
            throw new ForbiddenActionException();
        }
    }

    private long getProcessRoleId(long userId, long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }

    private String getRedirectUrl(ApplicationResource applicationResource) {
        return "redirect:" + APPLICATION_BASE_URL + applicationResource.getId();
    }
}
