package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;
import org.innovateuk.ifs.management.assessment.populator.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ApplicationAssessmentProgressRemoveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}/application/{applicationId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentApplicationProgressController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class AssessmentApplicationProgressController {

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @GetMapping
    public String applicationProgress(Model model,
                                      @PathVariable("applicationId") Long applicationId,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "assessorNameFilter", defaultValue = "") String assessorNameFilter,
                                      @RequestParam(value = "sort", defaultValue = "ASSESSOR") Sort sort) {
        return doProgressView(model, applicationId, assessorNameFilter, page, sort);
    }

    @PostMapping(path = "/assign/{assessorId}")
    public String assignAssessor(@PathVariable("competitionId") Long competitionId,
                                 @PathVariable("applicationId") Long applicationId,
                                 @PathVariable("assessorId") Long assessorId,
                                 @RequestParam(value = "sortField", defaultValue = "TITLE") String sortField) {
        assessmentRestService.createAssessment(new AssessmentCreateResource(applicationId, assessorId)).getSuccess();
        return format("redirect:/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, sortField);
    }

    @PostMapping("/withdraw/{assessmentId}")
    public String withdrawAssessment(@PathVariable("competitionId") Long competitionId,
                                     @PathVariable("applicationId") Long applicationId,
                                     @PathVariable("assessmentId") Long assessmentId,
                                     @RequestParam(value = "sortField", defaultValue = "TITLE") String sortField) {
        assessmentRestService.withdrawAssessment(assessmentId).getSuccess();
        return format("redirect:/assessment/competition/%s/application/%s/assessors?sortField=%s", competitionId, applicationId, sortField);
    }

    @GetMapping(value = "/withdraw/{assessmentId}/confirm")
    public String withdrawAssessmentConfirm(
            Model model,
            @PathVariable("competitionId") Long competitionId,
            @PathVariable("applicationId") Long applicationId,
            @PathVariable("assessmentId") Long assessmentId,
            @RequestParam(value = "sortField", defaultValue = "TITLE") String sortField) {
        model.addAttribute("model", new ApplicationAssessmentProgressRemoveViewModel(
                competitionId,
                applicationId,
                assessmentId,
                AvailableAssessorsSortFieldType.valueOf(sortField)
        ));
        return "competition/application-progress-remove-confirm";
    }

    private String doProgressView(Model model, Long applicationId, String assessorNameFilter, int page, Sort sort) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId, assessorNameFilter, page, sort));

        return "competition/application-progress";
    }
}
