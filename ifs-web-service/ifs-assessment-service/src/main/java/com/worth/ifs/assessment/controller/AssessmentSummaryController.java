package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
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
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Controller
public class AssessmentSummaryController extends AbstractApplicationController {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessOutcomeService processOutcomeService;

    @Autowired
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    private static String SUMMARY = "assessment/application-summary";

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.GET)
    public String getSummary(Model model,
                             HttpServletResponse response,
                             @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentSummaryForm form,
                             BindingResult bindingResult, @PathVariable("assessmentId") Long assessmentId) throws ExecutionException, InterruptedException {
        populateFormWithExistingValues(form, assessmentId);
        model.addAttribute("model", assessmentSummaryModelPopulator.populateModel(assessmentId));
        return SUMMARY;
    }

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.POST)
    public String save(Model model,
                       HttpServletResponse response,
                       @Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentSummaryForm form,
                       BindingResult bindingResult,
                       ValidationHandler validationHandler,
                       @PathVariable("assessmentId") Long assessmentId) {

        //TODO change implementation of lambda call to handle exceptions concisely
        Supplier<String> failureView = () -> {
            String view = "";
            try {
                view = getSummary(model, response, form, bindingResult, assessmentId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return view;
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = assessmentService.recommend(assessmentId, form.getFundingConfirmation(), form.getFeedback(), form.getComment());
            validationHandler.addAnyErrors(updateResult);

            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> redirectToCompetitionOfAssessment(assessmentId));
        });
    }

    private String redirectToCompetitionOfAssessment(Long assessmentId)  {
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