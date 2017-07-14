package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @GetMapping("/{assessorId}")
    public String assessmentProgress(@PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "innovationArea", required = false) Optional<Long> innovationArea,
                                     @RequestParam(value = "origin", defaultValue = "MANAGE_ASSESSMENTS") String origin,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     Model model) {

        String backUrl = buildBackUrl(origin, competitionId, queryParams);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.MANAGE_APPLICATIONS, queryParams);

        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId, page, innovationArea, originQuery));
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("backUrl", backUrl);

        return "competition/assessor-progress";
    }

    private String buildBackUrl(String origin, long competitionId, MultiValueMap<String, String> queryParams) {
        String baseUrl = CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.valueOf(origin).getBaseOriginUrl();
        queryParams.remove("origin");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap("competitionId", competitionId))
                .encode()
                .toUriString();
    }
}
