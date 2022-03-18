package org.innovateuk.ifs.management.assessor.controller;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * This controller will handle all Competition Management requests to view assessor profiles.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementAssessorProfileController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class CompetitionManagementAssessorProfileController {

    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;
    private AssessorRestService assessorRestService;

    public CompetitionManagementAssessorProfileController() {
    }

    @Autowired
    public CompetitionManagementAssessorProfileController(AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator,
                                                          AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator,
                                                          AssessorRestService assessorRestService) {
        this.assessorProfileSkillsModelPopulator = assessorProfileSkillsModelPopulator;
        this.assessorProfileDeclarationModelPopulator = assessorProfileDeclarationModelPopulator;
        this.assessorRestService = assessorRestService;
    }

    @GetMapping("/profile/{assessorId}")
    public String profile(@PathVariable("competitionId") long competitionId,
                          @PathVariable("assessorId") long assessorId) {

        return "redirect:/competition/" + competitionId + "/assessors/profile/" + assessorId + "?tab=skills";
    }

    @GetMapping(value = "/profile/{assessorId}", params = "tab=skills")
    public String profileSkills(Model model,
                                @PathVariable("competitionId") long competitionId,
                                @PathVariable("assessorId") long assessorId) {


        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileSkillsModelPopulator.populateModel(assessorProfile.getUser(), profile, Optional.of(competitionId), true));

        return "profile/skills";
    }

    @GetMapping(value = "/profile/{assessorId}", params = "tab=declaration")
    public String profileDeclaration(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId) {

        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(assessorProfile.getUser(), profile, Optional.of(competitionId), true));

        return "profile/declaration-of-interest";
    }
}
