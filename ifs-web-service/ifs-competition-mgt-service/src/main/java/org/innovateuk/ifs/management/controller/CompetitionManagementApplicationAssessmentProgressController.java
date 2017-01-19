package org.innovateuk.ifs.management.controller;

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

import static java.lang.String.format;

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

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) AvailableAssessorsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      @PathVariable("applicationId") Long applicationId) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId, form.getSortField()));
        return "competition/application-progress";
    }

    @RequestMapping(value = "/withdraw/{assessmentId}", method = RequestMethod.POST)
    public String withdrawAssessment(@PathVariable("competitionId") Long competitionId,
                                     @PathVariable("applicationId") Long applicationId,
                                     @PathVariable("assessmentId") Long assessmentId) {
        assessmentRestService.withdrawAssessment(assessmentId).getSuccessObjectOrThrowException();
        return format("redirect:/competition/%s/application/%s/assessors", competitionId, applicationId);
    }
}