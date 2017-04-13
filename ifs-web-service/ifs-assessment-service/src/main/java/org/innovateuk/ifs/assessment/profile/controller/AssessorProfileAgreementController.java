package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileAgreementModelPopulator;
import org.innovateuk.ifs.profile.service.ProfileService;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.UserResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to manage the Assessor Profile Agreement view.
 */
@Controller
@RequestMapping("/profile/agreement")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileAgreementController {

    @Autowired
    private AssessorProfileAgreementModelPopulator assessorProfileAgreementModelPopulator;

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public String getAgreement(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProfileAgreementResource profileAgreementResource = profileService.getProfileAgreement(loggedInUser.getId());
        return doViewAgreement(model, profileAgreementResource);
    }

    @PostMapping
    public String submitAgreement(Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        profileService.updateProfileAgreement(loggedInUser.getId()).getSuccessObjectOrThrowException();
        return "redirect:/assessor/dashboard";
    }

    private String doViewAgreement(Model model, ProfileAgreementResource profileAgreementResource) {
        model.addAttribute("model", assessorProfileAgreementModelPopulator.populateModel(profileAgreementResource));
        return "profile/agreement";
    }
}
