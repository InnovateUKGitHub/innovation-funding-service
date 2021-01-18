package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.assessmentperiod.model.ManageAssessmentPeriodsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessment-period")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentPeriodController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentPeriodController {

    @Autowired
    private ManageAssessmentPeriodsPopulator assessmentPeriodsPopulator;

    @GetMapping
    public String manageAssessmentPeriods(@PathVariable long competitionId, Model model){

        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId));
        return "competition/manage-assessment-periods";
    }

    @PostMapping
    public String submitAssessmentPeriods(@PathVariable long competitionId,
                                          Model model){


        return " ";
    }

}
