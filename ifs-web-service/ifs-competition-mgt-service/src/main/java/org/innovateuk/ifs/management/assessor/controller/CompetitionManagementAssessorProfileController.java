package org.innovateuk.ifs.management.assessor.controller;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests to view assessor profiles.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementAssessorProfileController.class)
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
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

    public enum AssessorProfileOrigin {
        APPLICATION_PROGRESS("/assessment/competition/{competitionId}/application/{applicationId}/assessors"),
        ASSESSOR_FIND("/competition/{competitionId}/assessors/find"),
        ASSESSOR_INVITE("/competition/{competitionId}/assessors/invite"),
        ASSESSOR_OVERVIEW("/competition/{competitionId}/assessors/pending-and-declined"),
        ASSESSOR_ACCEPTED("/competition/{competitionId}/assessors/accepted"),
        MANAGE_ASSESSORS("/assessment/competition/{competitionId}/assessors"),
        ASSESSOR_PROGRESS("/assessment/competition/{competitionId}/assessors/{assessorId}"),
        PANEL_FIND("/assessment/panel/competition/{competitionId}/assessors/find"),
        PANEL_INVITE("/assessment/panel/competition/{competitionId}/assessors/invite"),
        PANEL_OVERVIEW("/assessment/panel/competition/{competitionId}/assessors/pending-and-declined"),
        PANEL_ACCEPTED("/assessment/panel/competition/{competitionId}/assessors/accepted"),
        INTERVIEW_FIND("/assessment/interview/competition/{competitionId}/assessors/find"),
        INTERVIEW_INVITE("/assessment/interview/competition/{competitionId}/assessors/invite"),
        INTERVIEW_OVERVIEW("/assessment/interview/competition/{competitionId}/assessors/pending-and-declined"),
        INTERVIEW_ACCEPTED("/assessment/interview/competition/{competitionId}/assessors/accepted"),
        INTERVIEW_ALLOCATION("/assessment/interview/competition/{competitionId}/assessors/allocate-assessors");

        private String baseOriginUrl;

        AssessorProfileOrigin(String baseOriginUrl) {
            this.baseOriginUrl = baseOriginUrl;
        }

        public String getBaseOriginUrl() {
            return baseOriginUrl;
        }
    }

    @GetMapping("/profile/{assessorId}")
    public String profile(@PathVariable("competitionId") long competitionId,
                          @PathVariable("assessorId") long assessorId,
                          @RequestParam(value = "origin", defaultValue = "ASSESSOR_FIND") String origin,
                          @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.valueOf(origin), queryParams);

        return "redirect:/competition/" + competitionId + "/assessors/profile/" + assessorId + "/skills" + originQuery;
    }

    @GetMapping("/profile/{assessorId}/skills")
    public String profileSkills(Model model,
                                @PathVariable("competitionId") long competitionId,
                                @PathVariable("assessorId") long assessorId,
                                @RequestParam(value = "origin", defaultValue = "ASSESSOR_FIND") String origin,
                                @RequestParam(value = "applicationId", required = false) Long applicationId,
                                @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.valueOf(origin), queryParams);

        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileSkillsModelPopulator.populateModel(assessorProfile.getUser(), profile, Optional.of(competitionId), originQuery, true));
        model.addAttribute("backUrl", buildBackUrl(origin, competitionId, applicationId, assessorId, queryParams));

        return "profile/skills";
//        return "assessors/profile-skills";
    }

    @GetMapping("/profile/{assessorId}/declaration")
    public String profileDeclaration(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId,
                                     @RequestParam(value = "origin", defaultValue = "ASSESSOR_FIND") String origin,
                                     @RequestParam(value = "applicationId", required = false) Long applicationId,
                                     @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.valueOf(origin), queryParams);

        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();
        ProfileResource profile = assessorProfile.getProfile();

        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(assessorProfile.getUser(), profile, Optional.of(competitionId), originQuery, true));
        model.addAttribute("backUrl", buildBackUrl(origin, competitionId, applicationId, assessorId, queryParams));

        return "profile/declaration-of-interest";
    }

    private String buildBackUrl(String origin, Long competitionId, Long applicationId, Long assessorId, MultiValueMap<String, String> queryParams) {
        String baseUrl = AssessorProfileOrigin.valueOf(origin).getBaseOriginUrl();
        queryParams.remove("origin");

        if (queryParams.containsKey("applicationId")) {
            queryParams.remove("applicationId");
        }

        if (queryParams.containsKey("assessorId")) {
            queryParams.remove("assessorId");
        }

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap("competitionId", competitionId, "applicationId", applicationId, "assessorId", assessorId))
                .encode()
                .toUriString();
    }
}
