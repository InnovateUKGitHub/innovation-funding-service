package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.form.AssessmentSummaryForm;
import com.worth.ifs.assessment.model.AssessmentSummaryModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.workflow.ProcessOutcomeService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonFailureKeys.SUMMARY_COMMENT_WORD_LIMIT_EXCEEDED;
import static com.worth.ifs.commons.error.CommonFailureKeys.SUMMARY_FEEDBACK_WORD_LIMIT_EXCEEDED;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.mappingErrorKeyToField;

@Controller
public class AssessmentSummaryController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessOutcomeService processOutcomeService;

    @Autowired
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.GET)
    public String getSummary(Model model,
                             HttpServletResponse response,
                             @ModelAttribute(FORM_ATTR_NAME) AssessmentSummaryForm form,
                              @PathVariable("assessmentId") Long assessmentId) {
        populateFormWithExistingValues(form, assessmentId);
        model.addAttribute("model", assessmentSummaryModelPopulator.populateModel(assessmentId));
        return "assessment/application-summary";
    }

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.POST)
    public String save(Model model,
                       HttpServletResponse response,
                       @Valid @ModelAttribute(FORM_ATTR_NAME) AssessmentSummaryForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       ValidationHandler validationHandler,
                       @PathVariable("assessmentId") Long assessmentId) {

        Supplier<String> failureView = () -> getSummary(model, response, form, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = assessmentService.recommend(assessmentId, form.getFundingConfirmation(), form.getFeedback(), form.getComment());
            return validationHandler.addAnyErrors(updateResult,
                    mappingErrorKeyToField(SUMMARY_FEEDBACK_WORD_LIMIT_EXCEEDED, "feedback"),
                    mappingErrorKeyToField(SUMMARY_COMMENT_WORD_LIMIT_EXCEEDED, "comment"))
            .failNowOrSucceedWith(failureView, () -> redirectToCompetitionOfAssessment(assessmentId));
        });
    }

    private String redirectToCompetitionOfAssessment(Long assessmentId) {
        return "redirect:/assessor/dashboard/competition/" + getAssessment(assessmentId).getCompetition();
    }

    private void populateFormWithExistingValues(AssessmentSummaryForm form, Long assessmentId) {
        getOutcome(assessmentId).ifPresent(outcome -> {
            form.setFundingConfirmation(Optional.ofNullable(outcome.getOutcome()).map(BooleanUtils::toBoolean).orElse(null));
            form.setFeedback(outcome.getDescription());
            form.setComment(outcome.getComment());
        });
    }

    private Optional<ProcessOutcomeResource> getOutcome(Long assessmentId) {
        return getAssessment(assessmentId).getProcessOutcomes().stream().reduce((id1, id2) -> id2).map(id -> processOutcomeService.getById(id));
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }
}