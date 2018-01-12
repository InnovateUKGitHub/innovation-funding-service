package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage display of applications in panel review
 */
@Controller
@RequestMapping(value = "/review/{reviewId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentReviewController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentReviewApplicationSummaryController {

    @Autowired
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @GetMapping("application/{applicationId}")
    public String viewApplication(@PathVariable("applicationId") long applicationId,
                                 Model model) {
        model.addAttribute("model", assessmentReviewApplicationSummaryModelPopulator.populateModel(applicationId));
        return "assessor-panel-application-overview";
    }
}
