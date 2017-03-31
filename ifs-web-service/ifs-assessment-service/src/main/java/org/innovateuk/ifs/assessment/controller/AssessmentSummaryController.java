package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.form.AssessmentSummaryForm;
import org.innovateuk.ifs.assessment.model.AssessmentSummaryModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentSummaryController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    @GetMapping("/{assessmentId}/summary")
    public String getSummary(Model model,
                             @ModelAttribute(FORM_ATTR_NAME) AssessmentSummaryForm form,
                             BindingResult bindingResult,
                             @PathVariable("assessmentId") Long assessmentId) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(form, assessment);
        }
        model.addAttribute("model", assessmentSummaryModelPopulator.populateModel(assessment));
        return "assessment/application-summary";
    }

    @PostMapping("/{assessmentId}/summary")
    public String save(Model model,
                       @Valid @ModelAttribute(FORM_ATTR_NAME) AssessmentSummaryForm form,
                       BindingResult bindingResult,
                       ValidationHandler validationHandler,
                       @PathVariable("assessmentId") Long assessmentId) {

        Supplier<String> failureView = () -> getSummary(model, form, bindingResult, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = assessmentService.recommend(assessmentId, form.getFundingConfirmation(), form.getFeedback(), form.getComment());
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> redirectToCompetitionOfAssessment(assessmentId));
        });
    }

    private String redirectToCompetitionOfAssessment(Long assessmentId) {
        return "redirect:/assessor/dashboard/competition/" + getAssessment(assessmentId).getCompetition();
    }

    private void populateFormWithExistingValues(AssessmentSummaryForm form, AssessmentResource assessment) {
        ofNullable(assessment.getFundingDecision()).ifPresent(fundingDecision -> {
            form.setFundingConfirmation(fundingDecision.getFundingConfirmation());
            form.setFeedback(fundingDecision.getFeedback());
            form.setComment(fundingDecision.getComment());
        });
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }
}
