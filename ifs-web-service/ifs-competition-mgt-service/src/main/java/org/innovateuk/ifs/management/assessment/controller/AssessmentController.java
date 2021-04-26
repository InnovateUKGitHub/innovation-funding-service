package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

/**
 * Controller for the Manage Assessments dashboard.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentController {


    private static final String DEFAULT_PAGE = "0";

    private static final String DEFAULT_PAGE_SIZE = "2";

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @GetMapping
    public String manageAssessments(@RequestParam(defaultValue = DEFAULT_PAGE) int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, @PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", manageAssessmentsModelPopulator.populateModel(competitionId, page, pageSize));
        return "competition/manage-assessments";
    }

    @PostMapping("/assessment-period/{assessmentPeriodId}/notify-assessors")
    public String notifyAssessors(@RequestParam(defaultValue = DEFAULT_PAGE) int page, @PathVariable("competitionId")long competitionId, @PathVariable("assessmentPeriodId")long assessmentPeriodId){
        assessorRestService.notifyAssessorsByAssessmentPeriod(assessmentPeriodId).getSuccess();
        return format("redirect:/assessment/competition/%s?page=%s", competitionId, page);
    }

    @PostMapping("/assessment-period/{assessmentPeriodId}/close-assessment")
    public String closeAssessment(@RequestParam(defaultValue = DEFAULT_PAGE) int page, @PathVariable("competitionId")long competitionId, @PathVariable("assessmentPeriodId")long assessmentPeriodId){
        assessorRestService.closeAssessmentByAssessmentPeriod(assessmentPeriodId).getSuccess();
        return format("redirect:/assessment/competition/%s?page=%s", competitionId, page);
    }

}