package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.assessment.form.AssessmentApplicationSummaryForm;
import com.worth.ifs.assessment.model.AssessmentSummaryModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.workflow.ProcessOutcomeService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
public class AssessmentSummaryController extends AbstractApplicationController {

    private static Log LOG = LogFactory.getLog(AssessmentSummaryController.class);

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
                             @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentApplicationSummaryForm form,
                             BindingResult bindingResult, @PathVariable("assessmentId") Long assessmentId) throws ExecutionException, InterruptedException {
        populateFormWithExistingValues(form, assessmentId);
        model.addAttribute("model", assessmentSummaryModelPopulator.populateModel(assessmentId));
        return SUMMARY;
    }

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.POST)
    public String save(Model model,
                       HttpServletResponse response,
                       @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentApplicationSummaryForm form,
                       BindingResult bindingResult,
                       @PathVariable("assessmentId") Long assessmentId) {
        LOG.warn("AssessmentApplicationSummaryForm{" +
                "fundingConfirmation=" + form.getFundingConfirmation() +
                ", feedback='" + form.getFeedback() + '\'' +
                ", comments='" + form.getComments() +
                '}');
        // TODO validation
        // TODO service call
        // TODO handle service errors
        return "redirect:/" + assessmentId;
    }

    private void populateFormWithExistingValues(AssessmentApplicationSummaryForm form, Long assessmentId) {
        getOutcome(assessmentId).ifPresent(outcome -> {
            form.setFundingConfirmation(Boolean.valueOf(outcome.getOutcome()));
            form.setFeedback(outcome.getDescription());
            form.setComments(outcome.getComment());
        });
    }

    private Optional<ProcessOutcomeResource> getOutcome(Long assessmentId) {
        return getAssessment(assessmentId).getProcessOutcomes().stream().reduce((id1, id2) -> id2).map(id -> processOutcomeService.getById(id));
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }
}