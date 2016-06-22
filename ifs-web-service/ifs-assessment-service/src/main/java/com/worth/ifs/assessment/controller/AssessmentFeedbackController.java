package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Controller
public class AssessmentFeedbackController {

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;


    @RequestMapping(method = RequestMethod.GET, value = "/{processId}/question/{questionId}")
    public String getQuestion(Model model, HttpServletResponse response, @PathVariable("processId") final Long processId, @PathVariable("questionId") final Long questionId) {
        return "assessor-question";
    }
}
