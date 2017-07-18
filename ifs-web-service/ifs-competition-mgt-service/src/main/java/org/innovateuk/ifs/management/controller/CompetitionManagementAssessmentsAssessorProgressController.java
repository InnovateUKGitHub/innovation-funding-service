package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @GetMapping("/{assessorId}")
    public String assessorProgress(@PathVariable("competitionId") long competitionId,
                                   @PathVariable("assessorId") long assessorId,
                                   @RequestParam MultiValueMap<String, String> params,
                                   Model model) {
        params.add("assessorId", String.valueOf(assessorId));
        model.addAttribute("originQuery", buildOriginQueryString(ApplicationOverviewOrigin.ASSESSOR_PROGRESS, params));
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId));

        return "competition/assessor-progress";
    }
}
