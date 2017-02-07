package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.management.model.InviteAssessorProfileModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.util.MapFunctions.asMap;

public class CompetitionManagementAssessorProfileController extends BaseController {

    @Autowired
    private InviteAssessorProfileModelPopulator inviteAssessorProfileModelPopulator;

    enum AssessorProfileOrigin {
        VIEW_PROGRESS("/competition/{competitionId}/application/{applicationId}/assessors"),
        ASSESSOR_FIND("/competition/{competitionId}/assessors/find"),
        ASSESSOR_INVITE("/competition/{competitionId}/assessors/invite"),
        ASSESSOR_OVERVIEW("/competition/{competitionId}/assessors/overview");

        private String baseOriginUrl;

        AssessorProfileOrigin(String baseOriginUrl) {
            this.baseOriginUrl = baseOriginUrl;
        }

        public String getBaseOriginUrl() {
            return baseOriginUrl;
        }
    }

    public static String buildOriginQueryString(AssessorProfileOrigin origin, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.newInstance()
                .queryParam("origin", origin.toString())
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
    }

    @RequestMapping(value = "/profile/{assessorId}", method = RequestMethod.GET)
    public String profile(Model model,
                          @PathVariable("competitionId") long competitionId,
                          @PathVariable("assessorId") long assessorId,
                          @RequestParam(value = "origin", defaultValue = "VIEW_PROGRESS") String origin,
                          @RequestParam MultiValueMap<String, String> queryParams) {

        model.addAttribute("model", inviteAssessorProfileModelPopulator.populateModel(assessorId, competitionId));
        model.addAttribute("backUrl", buildBackUrl(origin, competitionId, queryParams));

        return "assessors/profile";
    }

    private String buildBackUrl(String origin, Long competitionId, MultiValueMap<String, String> queryParams) {
        String baseUrl = AssessorProfileOrigin.valueOf(origin).getBaseOriginUrl();

        queryParams.remove("origin");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap("competitionId", competitionId))
                .encode()
                .toUriString();
    }
}
