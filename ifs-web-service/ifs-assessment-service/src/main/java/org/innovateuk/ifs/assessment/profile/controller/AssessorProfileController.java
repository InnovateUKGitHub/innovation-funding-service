package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

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
    private ProfileRestService profileRestService;
    private AssessorRestService assessorRestService;

    public AssessorProfileController(AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator,
                                     AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator,
                                     ProfileRestService profileRestService,
                                     AssessorRestService assessorRestService) {
        this.assessorProfileSkillsModelPopulator = assessorProfileSkillsModelPopulator;
        this.assessorProfileDeclarationModelPopulator = assessorProfileDeclarationModelPopulator;
        this.profileRestService = profileRestService;
        this.assessorRestService = assessorRestService;
    }

    @GetMapping("/skills")
    public String skills(Model model,
                         UserResource loggedInUser) {

        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(loggedInUser.getId()).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileSkillsModelPopulator.populateModel(loggedInUser, profile, null, null, false));
        return "profile/skills";
    }

    @GetMapping("/declaration")
    public String profileDeclaration(Model model,
                                     UserResource loggedInUser) {

        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(loggedInUser.getId()).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(loggedInUser, profile, null, null, false));
        return "profile/declaration-of-interest";
    }

}
