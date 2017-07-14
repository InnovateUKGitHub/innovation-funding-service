package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static java.util.Optional.of;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    private static final int PAGE_SIZE = 20;

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @GetMapping("/{assessorId}")
    public String assessmentProgress(@PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "innovationArea", required = false) Optional<Long> innovationArea,
                                     Model model) {

        ApplicationCountSummaryPageResource applicationCounts = getApplicationCounts(competitionId, page, innovationArea);
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId));

        return "competition/assessor-progress";
    }

    private ApplicationCountSummaryPageResource getApplicationCounts(long competitionId, int page, Optional<Long> innovationArea) {
        return applicationCountSummaryRestService
                .getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionId, page, PAGE_SIZE, innovationArea)
                .getSuccessObjectOrThrowException();
    }
}
