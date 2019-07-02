package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

/**
 * Controller to manage display of applications in panel review
 */
@Controller
@RequestMapping(value = "/review/{reviewId}")
@SecuredBySpring(value = "Controller", description = "Assessors can access applications for review", securedType = AssessmentReviewController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentReviewApplicationSummaryController {

    @Autowired
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @GetMapping("/application/{applicationId}")
    public String viewApplication(@PathVariable("applicationId") long applicationId,
                                  @PathVariable("reviewId") long reviewId,
                                  @ModelAttribute("form") ApplicationForm form,
                                  @RequestParam MultiValueMap<String, String> queryParams,
                                  Model model,
                                  UserResource user) {
        queryParams.put("reviewId", singletonList(valueOf(reviewId)));
        queryParams.put("applicationId", singletonList(valueOf(applicationId)));
        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.PANEL_ASSESSOR_REVIEW, queryParams);
        model.addAttribute("model", assessmentReviewApplicationSummaryModelPopulator.populateModel(form, user, applicationId, originQuery));

        return "assessor-panel-application-overview";
    }
}
