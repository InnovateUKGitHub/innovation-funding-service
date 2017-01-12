package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/competition/{competitionId}/application/{applicationId}/assessors")
public class CompetitionManagementApplicationAssessmentProgressController {

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model, @PathVariable("applicationId") Long applicationId) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId));
        return "competition/application-progress";
    }

}