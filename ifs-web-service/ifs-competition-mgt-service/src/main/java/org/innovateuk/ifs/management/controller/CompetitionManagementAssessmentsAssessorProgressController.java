package org.innovateuk.ifs.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @GetMapping("/{assessorId}")
    public String assessmentProgress() {
        return "competition/assessor-progress";
    }
}
