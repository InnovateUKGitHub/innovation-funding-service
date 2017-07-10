package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @GetMapping("/{assessorId}")
    public String assessmentProgress(@PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId,
                                     Model model) {
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId));

        return "competition/assessor-progress";
    }
}
