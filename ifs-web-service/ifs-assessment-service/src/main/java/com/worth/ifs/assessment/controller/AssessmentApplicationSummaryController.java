package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.assessment.form.AssessmentApplicationSummaryForm;
import com.worth.ifs.assessment.model.AssessmentApplicationSummaryModelPopulator;
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
import java.util.concurrent.ExecutionException;

@Controller
public class AssessmentApplicationSummaryController extends AbstractApplicationController {

    private static Log LOG = LogFactory.getLog(AssessmentApplicationSummaryController.class);

    @Autowired
    private AssessmentApplicationSummaryModelPopulator assessmentApplicationSummaryModelPopulator;

    private static String SUMMARY = "assessment/application-summary";

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.GET)
    public String getSummary(Model model,
                             HttpServletResponse response,
                             @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentApplicationSummaryForm form,
                             BindingResult bindingResult, @PathVariable("assessmentId") Long assessmentId) throws ExecutionException, InterruptedException {
        // TODO populate the form with any existing values
        model.addAttribute("model", assessmentApplicationSummaryModelPopulator.populateModel(assessmentId));
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
}
