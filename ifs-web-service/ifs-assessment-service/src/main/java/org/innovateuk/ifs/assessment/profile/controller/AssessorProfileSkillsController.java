package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.profile.form.AssessorProfileSkillsForm;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileEditSkillsModelPopulator;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Skills page
 */
@Controller
@RequestMapping("/profile/skills")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileSkillsController {

    @Autowired
    private ProfileRestService profileRestService;

    @Autowired
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Autowired
    private AssessorProfileEditSkillsModelPopulator assessorProfileEditSkillsModelPopulator;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getReadonlySkills(Model model,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProfileSkillsResource profileSkillsResource = profileRestService.getProfileSkills(loggedInUser.getId()).getSuccessObjectOrThrowException();
        model.addAttribute("model", assessorProfileSkillsModelPopulator.populateModel(profileSkillsResource));
        return "profile/skills";
    }

    @GetMapping("/edit")
    public String getSkills(Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            @ModelAttribute(FORM_ATTR_NAME) AssessorProfileSkillsForm form,
                            BindingResult bindingResult) {
        return doViewEditSkills(model, loggedInUser, form, bindingResult);
    }

    @PostMapping("/edit")
    public String submitSkills(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileSkillsForm form,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewEditSkills(model, loggedInUser, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ProfileSkillsEditResource profileSkillsEditResource = new ProfileSkillsEditResource();
            profileSkillsEditResource.setBusinessType(form.getAssessorType());
            profileSkillsEditResource.setSkillsAreas(form.getSkillAreas());
            ServiceResult<Void> result = profileRestService.updateProfileSkills(loggedInUser.getId(), profileSkillsEditResource).toServiceResult();

            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/profile/skills");
        });
    }

    private String doViewEditSkills(Model model, UserResource loggedInUser,
                                    AssessorProfileSkillsForm form, BindingResult bindingResult) {
        ProfileSkillsResource profileSkillsResource = profileRestService.getProfileSkills(loggedInUser.getId()).getSuccessObjectOrThrowException();
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(profileSkillsResource, form);
        }
        model.addAttribute("model", assessorProfileEditSkillsModelPopulator.populateModel(profileSkillsResource));
        return "profile/skills-edit";
    }

    private void populateFormWithExistingValues(ProfileSkillsResource profileSkillsResource, AssessorProfileSkillsForm form) {
        form.setAssessorType(profileSkillsResource.getBusinessType());
        form.setSkillAreas(profileSkillsResource.getSkillsAreas());
    }
}
