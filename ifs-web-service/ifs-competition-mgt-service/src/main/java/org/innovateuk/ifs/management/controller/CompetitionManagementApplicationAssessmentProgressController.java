package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
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

    @Autowired
    private AssessmentRestService assessmentRestService;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model, @PathVariable("applicationId") Long applicationId) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId));
        return "competition/application-progress";
    }

    @RequestMapping(path = "/assign/{assessorId}", method = RequestMethod.POST)
    public String assignAssessor(Model model, @PathVariable("applicationId") Long applicationId, @PathVariable("assessorId") Long assessorId) {
        assessmentRestService.createAssessment(new AssessmentCreateResource(applicationId, assessorId));

        return applicationProgress(model, applicationId);
    }
}
