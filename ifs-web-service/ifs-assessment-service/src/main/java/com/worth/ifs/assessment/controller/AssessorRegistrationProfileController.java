package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.form.AssessorRegistrationDeclarationForm;
import com.worth.ifs.assessment.form.AssessorRegistrationSkillsForm;
import com.worth.ifs.assessment.form.AssessorRegistrationTermsForm;
import com.worth.ifs.assessment.model.AssessorRegistrationDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationTermsModelPopulator;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage Assessor Registration profile pages
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationProfileController {

    @Autowired
    private AssessorRegistrationSkillsModelPopulator assessorSkillsModelPopulator;

    @Autowired
    private AssessorRegistrationDeclarationModelPopulator assessorRegistrationDeclarationModelPopulator;

    @Autowired
    private AssessorRegistrationTermsModelPopulator assessorRegistrationTermsModelPopulator;

    @Autowired
    private UserService userService;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(value = "skills", method = RequestMethod.GET)
    public String getSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form) {
        return doViewYourSkills(model);
    }

    @RequestMapping(value = "skills", method = RequestMethod.POST)
    public String submitSkills(Model model,
                               @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourSkills(model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ProfileResource profile = new ProfileResource();
            profile.setBusinessType(form.getAssessorType());
            profile.setSkillsAreas(form.getSkillAreas());
            ServiceResult<UserResource> result = userService.updateProfile(loggedInUser.getId(), profile);
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/registration/declaration");
        });

    }

    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String getDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationDeclarationForm form) {
        model.addAttribute("model", assessorRegistrationDeclarationModelPopulator.populateModel());
        return "registration/declaration-of-interest";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.POST)
    public String submitDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationDeclarationForm form) {
        return "redirect:/registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public String getTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationTermsForm form) {
        model.addAttribute("model", assessorRegistrationTermsModelPopulator.populateModel());
        return "registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.POST)
    public String submitTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationTermsForm form) {
        return "redirect:/assessor/dashboard";
    }

    private String doViewYourSkills(Model model) {
        model.addAttribute("model", assessorSkillsModelPopulator.populateModel());
        return "registration/innovation-areas";
    }
}
