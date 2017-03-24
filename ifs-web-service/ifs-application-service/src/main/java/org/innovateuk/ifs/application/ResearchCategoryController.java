package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.finance.view.FundingLevelResetHandler;
import org.innovateuk.ifs.application.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.populator.ApplicationResearchCategoryPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.UserResource;
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

/**
 * This controller handles requests by Applicants to change the research category choice for an Application.
 */
@Controller
@RequestMapping(ApplicationFormController.APPLICATION_BASE_URL+"{applicationId}/form/question/{questionId}/research-category")
@PreAuthorize("hasAuthority('applicant')")
public class ResearchCategoryController {

    private static String APPLICATION_SAVED_MESSAGE = "applicationSaved";

    @Autowired
    private ApplicationResearchCategoryPopulator researchCategoryPopulator;

    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping
    public String getResearchCategories(Model model, @PathVariable Long applicationId, @PathVariable Long questionId,
                                        HttpServletRequest request) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        ResearchCategoryViewModel researchCategoryViewModel = researchCategoryPopulator.populate(applicationId, questionId, user.getId());

        model.addAttribute("model", researchCategoryViewModel);

        return "application/research-categories";
    }

    @PostMapping
    public String submitResearchCategoryChoice(@ModelAttribute("form") @Valid ResearchCategoryForm researchCategoryForm,
                                               HttpServletRequest request, HttpServletResponse response,
                                               BindingResult bindingResult, ValidationHandler validationHandler,
                                               Model model, @PathVariable Long applicationId, @PathVariable Long questionId) {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        ResearchCategoryViewModel researchCategoryViewModel = researchCategoryPopulator.populate(applicationId, questionId, user.getId());

        model.addAttribute("model", researchCategoryViewModel);

        Supplier<String> failureView = () -> "application/research-categories";

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            return validationHandler.addAnyErrors(saveResearchCategoryChoice(applicationId, researchCategoryForm)).failNowOrSucceedWith(failureView,
                    () -> {cookieFlashMessageFilter.setFlashMessage(response, APPLICATION_SAVED_MESSAGE);
                        return "redirect:/application/"+applicationId+"/form/question/"+questionId;
                    });
        });
    }

    private RestResult<ApplicationResource> saveResearchCategoryChoice(Long applicationId, ResearchCategoryForm researchCategoryForm) {

        Long researchCategoryId = Long.valueOf(researchCategoryForm.getResearchCategoryChoice());

        return applicationResearchCategoryRestService.saveApplicationResearchCategoryChoice(applicationId, researchCategoryId);
    }
}
