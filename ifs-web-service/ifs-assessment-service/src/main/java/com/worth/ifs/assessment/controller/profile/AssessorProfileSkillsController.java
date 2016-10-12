package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileSkillsForm;
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

import javax.validation.Valid;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Skills page
 */
@Controller
@RequestMapping("/profile/skills")
public class AssessorProfileSkillsController {

    @Autowired
    private UserService userService;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getSkills(Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            @ModelAttribute(FORM_ATTR_NAME) AssessorProfileSkillsForm form,
                            BindingResult bindingResult) {
        return doViewYourSkills(loggedInUser, model, form, bindingResult);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitSkills(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileSkillsForm form,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewYourSkills(loggedInUser, model, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ProfileResource profile = new ProfileResource();
            profile.setBusinessType(form.getAssessorType());
            profile.setSkillsAreas(form.getSkillAreas());
            ServiceResult<Void> result = userService.updateProfile(loggedInUser.getId(), profile);
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewYourSkills(UserResource loggedInUser, Model model, AssessorProfileSkillsForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(loggedInUser, form);
        }
        return "profile/innovation-areas";
    }

    private void populateFormWithExistingValues(UserResource loggedInUser, AssessorProfileSkillsForm form) {
        if (loggedInUser.getProfile() != null) {
            form.setAssessorType(loggedInUser.getProfile().getBusinessType());
            form.setSkillAreas(loggedInUser.getProfile().getSkillsAreas());
        }
    }
}
