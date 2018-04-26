package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.review.model.ReviewModelPopulator;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * Controller for the Manage Assessment Panel dashboard.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can view the Manage Assessment Panel dashboard", securedType = ReviewController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'REVIEW')")
public class ReviewController {

    @Autowired
    private ReviewModelPopulator reviewModelPopulator;

    @Autowired
    private ReviewRestService reviewRestService;

    @GetMapping
    public String assessmentPanel(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", reviewModelPopulator.populateModel(competitionId));

        return "competition/manage-assessment-panel";
    }

    @PostMapping("/notify-assessors")
    public String notifyAssessors(@PathVariable("competitionId") long competitionId) {
        reviewRestService.notifyAssessors(competitionId).getSuccess();
        return format("redirect:/assessment/panel/competition/%d", competitionId);
    }
}
