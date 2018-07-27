package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage the Assessor Profile pages
 */
@Controller
@RequestMapping("/profile/details")
@SecuredBySpring(value = "Controller", description = "Assessor can view their profile and edit it", securedType = AssessorProfileDetailsEditController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileController {

    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    public AssessorProfileController(AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator,
                                     AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator) {
        this.assessorProfileSkillsModelPopulator = assessorProfileSkillsModelPopulator;
        this.assessorProfileDeclarationModelPopulator = assessorProfileDeclarationModelPopulator;
    }

    @GetMapping("/skills")
    public String skills(Model model,
                             UserResource loggedInUser) {

        model.addAttribute("model", assessorProfileSkillsModelPopulator.populateModel(loggedInUser));
        return "profile/skills";
    }

    @GetMapping("/declaration")
    public String profileDeclaration(Model model,
                                     UserResource loggedInUser) {

        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(loggedInUser));
        return "profile/declaration-of-interest";
    }

}
