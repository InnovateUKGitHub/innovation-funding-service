package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.management.form.AvailableAssessorsForm;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/competition/{competitionId}/application/{applicationId}/assessors")
public class CompetitionManagementApplicationAssessmentProgressController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) AvailableAssessorsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      @PathVariable("applicationId") Long applicationId) {
        return doProgressView(model, applicationId, form.getSort());
    }

    @RequestMapping(path = "/assign/{assessorId}", method = RequestMethod.POST)
    public String assignAssessor(Model model,
                                 @Valid @ModelAttribute(FORM_ATTR_NAME) AvailableAssessorsForm form,
                                 @PathVariable("applicationId") Long applicationId,
                                 @PathVariable("assessorId") Long assessorId) {
        assessmentRestService.createAssessment(new AssessmentCreateResource(applicationId, assessorId));
        return doProgressView(model, applicationId, form.getSort());
    }

    private String doProgressView(Model model, Long applicationId, String sort) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId, sort));
        return "competition/application-progress";
    }
}
