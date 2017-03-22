package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.assessment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.model.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.model.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static java.lang.String.format;

/**
 * Controller to manage assignment of Applications
 */
@Controller
@RequestMapping(value = "/{assessmentId}")
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentAssignmentController extends BaseController {

    @Autowired
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Autowired
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Autowired
    private AssessmentService assessmentService;


    @RequestMapping(value = "assignment", method = RequestMethod.GET)
    public String viewAssignment(@PathVariable("assessmentId") Long assessmentId,
                                 @ModelAttribute("form") AssessmentAssignmentForm form,
                                 Model model) {
        model.addAttribute("model", assessmentAssignmentModelPopulator.populateModel(assessmentId));
        return "assessment/assessment-invitation";
    }

    @RequestMapping(value = "assignment/accept", method = RequestMethod.POST)
    public String acceptAssignment(@PathVariable("assessmentId") Long assessmentId) {
        AssessmentResource assessment = assessmentService.getAssignableById(assessmentId);
        assessmentService.acceptInvitation(assessment.getId());
        return redirectToAssessorCompetitionDashboard(assessment.getCompetition());
    }

    @RequestMapping(value = "assignment/reject", method = RequestMethod.POST)
    public String rejectAssignment(Model model,
                                   @PathVariable("assessmentId") Long assessmentId,
                                   @Valid @ModelAttribute("form") AssessmentAssignmentForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewRejectAssignmentConfirm(model, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessmentResource assessment = assessmentService.getRejectableById(assessmentId);
            ServiceResult<Void> updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    @RequestMapping(value = "assignment/reject/confirm", method = RequestMethod.GET)
    public String rejectAssignmentConfirm(Model model,
                                          @ModelAttribute("form") AssessmentAssignmentForm form,
                                          @PathVariable("assessmentId") Long assessmentId) {
        return doViewRejectAssignmentConfirm(model, assessmentId);
    }

    private String doViewRejectAssignmentConfirm(Model model, Long assessmentId) {
        model.addAttribute("model", rejectAssessmentModelPopulator.populateModel(assessmentId));
        return "assessment/reject-invitation-confirm";
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
