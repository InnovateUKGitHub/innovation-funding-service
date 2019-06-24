package org.innovateuk.ifs.project.financereviewer.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.financereviewer.transactional.FinanceReviewerService;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle assigning and getting finances reviewers for a project.
 */
@RestController
@RequestMapping("/finance-reviewer")
public class FinanceReviewerController {

    @Autowired
    private FinanceReviewerService financeReviewerService;

    @GetMapping("/find-all")
    public RestResult<List<SimpleUserResource>> findAll() {
        return financeReviewerService.findFinanceUsers().toGetResponse();
    }

    @GetMapping(params = "projectId")
    public RestResult<SimpleUserResource> getFinanceReviewer(@RequestParam long projectId) {
        return financeReviewerService.getFinanceReviewerForProject(projectId).toGetResponse();
    }

    @PostMapping("/{userId}/assign/{projectId}")
    public RestResult<Void> assignFinanceReviewer(@PathVariable long userId, @PathVariable long projectId) {
        return financeReviewerService.assignFinanceReviewer(userId, projectId).toPostResponse();
    }
}
