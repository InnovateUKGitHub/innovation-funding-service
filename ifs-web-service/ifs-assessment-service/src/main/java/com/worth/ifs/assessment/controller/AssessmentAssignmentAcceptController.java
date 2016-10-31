package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage acceptance of a application assignments
 */
@Controller
@RequestMapping(value = "/{assessmentId}")
public class AssessmentAssignmentAcceptController extends BaseController {

    @RequestMapping(value = "assignment/accepted", method = RequestMethod.GET)
    public String acceptAssignment(@PathVariable("assessmentId") String assessmentId) {
     //   inviteRestService.acceptInvite(inviteHash).getSuccessObjectOrThrowException();
        return "redirect:/assessor/assessor-competition-dashboard";
    }
}
