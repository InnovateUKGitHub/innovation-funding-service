package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.application.review.populator.ReviewAndSubmitViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/application")
public class ReviewAndSubmitController {

    @Autowired
    private ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator;

    @SecuredBySpring(value = "READ", description = "Applicants can review and submit their applications")
    @PreAuthorize("hasAnyAuthority('applicant')")
    @GetMapping("/{applicationId}/review-and-submit")
    public String reviewAndSubmit(@PathVariable long applicationId,
                                  Model model,
                                  UserResource user) {
        model.addAttribute("model", reviewAndSubmitViewModelPopulator.populate(applicationId, user));
        return "application/review-and-submit";
    }
}
