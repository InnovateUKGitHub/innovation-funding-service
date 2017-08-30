package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.AssessorAssessmentProgressRemoveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @GetMapping("/{assessorId}")
    public String assessorProgress(@PathVariable("competitionId") long competitionId,
                                   @PathVariable("assessorId") long assessorId,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "innovationArea", required = false) Optional<Long> innovationArea,
                                   @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                   @RequestParam(value = "filterSearch", required = false) Optional<String> filter,
                                   @RequestParam MultiValueMap<String, String> params,
                                   Model model) {
        params.add("assessorId", String.valueOf(assessorId));
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.ASSESSOR_PROGRESS, params);
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId, page, innovationArea, sortField, filter.map(String::trim).orElse(""), originQuery));

        return "competition/assessor-progress";
    }

    @PostMapping("/{assessorId}/withdraw/{assessmentId}")
    public String withdrawAssessment(@PathVariable("competitionId") long competitionId,
                                     @PathVariable("assessorId") long assessorId,
                                     @PathVariable("assessmentId") long assessmentId,
                                     @RequestParam(value = "sortField", defaultValue = "") String sortField) {
        assessmentRestService.withdrawAssessment(assessmentId).getSuccessObjectOrThrowException();
        return format("redirect:/assessment/competition/%s/assessors/%s?sortField=%s", competitionId, assessorId, sortField);
    }

    @GetMapping(value = "/{assessorId}/withdraw/{assessmentId}/confirm")
    public String withdrawAssessmentConfirm(
            Model model,
            @PathVariable("competitionId") Long competitionId,
            @PathVariable("assessorId") long assessorId,
            @PathVariable("assessmentId") Long assessmentId,
            @RequestParam(value = "sortField", defaultValue = "") String sortField) {
        model.addAttribute("model", new AssessorAssessmentProgressRemoveViewModel(
                competitionId,
                assessorId,
                assessmentId,
                sortField
        ));
        return "competition/assessor-progress-remove-confirm";
    }

    @PostMapping("/{assessorId}/application/{applicationId}/assign")
    public String assessorAssign(@PathVariable("competitionId") long competitionId,
                                 @PathVariable("assessorId") long assessorId,
                                 @PathVariable("applicationId") long applicationId) {

        AssessmentCreateResource assessment = new AssessmentCreateResource(applicationId, assessorId);
        assessmentRestService.createAssessment(assessment).
                getSuccessObjectOrThrowException();

        return format("redirect:/assessment/competition/%s/assessors/%s", competitionId, assessorId);
    }
}
