package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorProfileModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests to view assessor profiles.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@PreAuthorize("hasAuthority('comp_admin')")
public class CompetitionManagementAssessorProfileController {

    @Autowired
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    enum AssessorProfileOrigin {
        APPLICATION_PROGRESS("/assessment/competition/{competitionId}/application/{applicationId}/assessors"),
        ASSESSOR_FIND("/competition/{competitionId}/assessors/find"),
        ASSESSOR_INVITE("/competition/{competitionId}/assessors/invite"),
        ASSESSOR_OVERVIEW("/competition/{competitionId}/assessors/overview"),
        MANAGE_ASSESSORS("/assessment/competition/{competitionId}/assessors"),
        ASSESSOR_PROGRESS("/assessment/competition/{competitionId}/assessors/{assessorId}"),
        PANEL_FIND("/assessment/panel/competition/{competitionId}/assessors/find"),
        PANEL_INVITE("/assessment/panel/competition/{competitionId}/assessors/find");

        private String baseOriginUrl;

        AssessorProfileOrigin(String baseOriginUrl) {
            this.baseOriginUrl = baseOriginUrl;
        }

        public String getBaseOriginUrl() {
            return baseOriginUrl;
        }
    }

    @GetMapping("/profile/{assessorId}")
    public String profile(Model model,
                          @PathVariable("competitionId") long competitionId,
                          @PathVariable("assessorId") long assessorId,
                          @RequestParam(value = "origin", defaultValue = "APPLICATION_PROGRESS") String origin,
                          @RequestParam(value = "applicationId", required = false) Long applicationId,
                          @RequestParam MultiValueMap<String, String> queryParams) {

        model.addAttribute("model", assessorProfileModelPopulator.populateModel(assessorId, competitionId));
        model.addAttribute("backUrl", buildBackUrl(origin, competitionId, applicationId, assessorId, queryParams));

        return "assessors/profile";
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
