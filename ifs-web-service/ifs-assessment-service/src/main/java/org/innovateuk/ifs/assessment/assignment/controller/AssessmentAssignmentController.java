package org.innovateuk.ifs.assessment.assignment.controller;

import org.innovateuk.ifs.assessment.assignment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.assignment.populator.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage assignment of Applications
 */
@Controller
@RequestMapping(value = "/{assessmentId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentAssignmentController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentAssignmentController {

    @Autowired
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Autowired
    private AssessmentService assessmentService;


    @GetMapping("assignment")
    public String viewAssignment(@PathVariable("assessmentId") Long assessmentId,
                                 @ModelAttribute(name = "form", binding = false) AssessmentAssignmentForm form,
                                 Model model) {
        model.addAttribute("model", assessmentAssignmentModelPopulator.populateModel(assessmentId));
        return "assessment/assessment-invitation";
    }

    @PostMapping("assignment/respond")
    public String respondToAssignment(Model model,
                                      @PathVariable("assessmentId") long assessmentId,
                                      @Valid @ModelAttribute("form") AssessmentAssignmentForm form,
                                      @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                      ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> viewAssignment(assessmentId, form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessmentResource assessment = assessmentService.getAssignableById(assessmentId);
            ServiceResult<Void> updateResult;
            if (form.getAssessmentAccept()) {
                updateResult = assessmentService.acceptInvitation(assessment.getId());
            } else {
                updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());
            }
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
