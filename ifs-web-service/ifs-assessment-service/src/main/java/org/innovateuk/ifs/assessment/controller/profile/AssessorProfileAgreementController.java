package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.assessment.model.profile.AssessorProfileAgreementModelPopulator;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String getAgreement(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProfileAgreementResource profileAgreementResource = userService.getProfileAgreement(loggedInUser.getId());
        return doViewAgreement(model, profileAgreementResource);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitAgreement(Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        userService.updateProfileAgreement(loggedInUser.getId()).getSuccessObjectOrThrowException();
        return "redirect:/assessor/dashboard";
    }

    private String doViewAgreement(Model model, ProfileAgreementResource profileAgreementResource) {
        model.addAttribute("model", assessorProfileAgreementModelPopulator.populateModel(profileAgreementResource));
        return "profile/agreement";
    }
}
