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
@RequestMapping("/assign-accept")
public class AssessmentAssignmentAcceptController extends BaseController {

    @RequestMapping(value = "application/{id}/accept", method = RequestMethod.GET)
    public String acceptInvite(@PathVariable("id") String id) {
     //   inviteRestService.acceptInvite(inviteHash).getSuccessObjectOrThrowException();
        return "redirect:/assessor/assessor-competition-dashboard";
    }
}
