package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.model.AssessmentApplicationSummaryModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.ExecutionException;

@Controller
public class AssessmentApplicationSummaryController {

    @Autowired
    private AssessmentApplicationSummaryModelPopulator assessmentApplicationSummaryModelPopulator;

    private static String SUMMARY = "assessor-application-summary";

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.GET)
    public String getSummary(@PathVariable("assessmentId") Long assessmentId, Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("model", assessmentApplicationSummaryModelPopulator.populateModel(assessmentId));
        return SUMMARY;
    }
}
