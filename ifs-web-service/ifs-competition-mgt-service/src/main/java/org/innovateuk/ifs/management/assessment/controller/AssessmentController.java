package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Manage Assessments dashboard.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentController {

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @GetMapping
    public String manageAssessments(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", manageAssessmentsModelPopulator.populateModel(competitionId));

        return "competition/manage-assessments";
    }

    @PostMapping("/assessment-period/{assessmentPeriodId}/notify-assessors")
    public String notifyAssessors(@PathVariable("competitionId")long competitionId, @PathVariable("assessmentPeriodId")long assessmentPeriodId){
        assessorRestService.notifyAssessorsByAssessmentPeriod(assessmentPeriodId).getSuccess();
        return String.format("redirect:/assessment/competition/%s", competitionId);
    }

}