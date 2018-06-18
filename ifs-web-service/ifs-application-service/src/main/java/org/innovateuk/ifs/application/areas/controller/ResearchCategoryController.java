package org.innovateuk.ifs.application.areas.controller;

import org.innovateuk.ifs.application.areas.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryPopulator;
import org.innovateuk.ifs.application.forms.validator.ApplicationDetailsEditableValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * This controller handles requests by Applicants to change the research category choice for an Application.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/research-category")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ResearchCategoryController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ResearchCategoryController {
    private static String APPLICATION_SAVED_MESSAGE = "applicationSaved";

    @Autowired
    private ApplicationResearchCategoryPopulator researchCategoryPopulator;

    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private ApplicationDetailsEditableValidator applicationDetailsEditableValidator;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getResearchCategories(Model model,
                                        @ModelAttribute(FORM_ATTR_NAME) ResearchCategoryForm researchCategoryForm,
                                        @PathVariable long applicationId,
                                        @PathVariable long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        if (!applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)) {
            throw new ForbiddenActionException();
        }

        model.addAttribute("model", researchCategoryPopulator.populate(applicationResource, questionId));
        populateForm(applicationResource, researchCategoryForm);

        return "application/research-categories";
    }

    @PostMapping
    public String submitResearchCategoryChoice(@ModelAttribute(FORM_ATTR_NAME) @Valid ResearchCategoryForm
                                                           researchCategoryForm,
                                               BindingResult bindingResult,
                                               HttpServletResponse response,
                                               ValidationHandler validationHandler,
                                               Model model,
                                               @PathVariable long applicationId,
                                               @PathVariable long questionId) {

        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        Supplier<String> failureView = () -> getResearchCategories(model, researchCategoryForm, applicationId,
                questionId);

        return validationHandler.addAnyErrors(saveResearchCategoryChoice(applicationId, researchCategoryForm))
                .failNowOrSucceedWith(failureView, () -> {
                    cookieFlashMessageFilter.setFlashMessage(response, APPLICATION_SAVED_MESSAGE);
                    return "redirect:/application/" + applicationId + "/form/question/" + questionId;
                });
    }

    private void populateForm(ApplicationResource applicationResource, ResearchCategoryForm form) {
        if (applicationResource.getResearchCategory() != null) {
            form.setResearchCategory(applicationResource.getResearchCategory().getId());
        }
    }

    private ServiceResult<ApplicationResource> saveResearchCategoryChoice(Long applicationId, ResearchCategoryForm
            researchCategoryForm) {
        return applicationResearchCategoryRestService.saveApplicationResearchCategoryChoice(applicationId,
                researchCategoryForm.getResearchCategory()).toServiceResult();
    }

    private void checkIfAllowed(long questionId, ApplicationResource applicationResource) throws
            ForbiddenActionException {
        if (!applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId,
                applicationResource)) {
            throw new ForbiddenActionException();
        }
    }
}
