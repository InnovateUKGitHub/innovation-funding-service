package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationFundingService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Responsible for receiving calls to set the funding decision for all applications for a given competition.
 */
@RestController
@RequestMapping("/applicationfunding")
public class ApplicationFundingDecisionController {

    @Autowired
    private ApplicationFundingService applicationFundingService;

    @Autowired
    private ProjectService projectService;

    @PostMapping(value="/sendNotifications")
    public RestResult<Void> sendFundingDecisions(@RequestBody FundingNotificationResource fundingNotificationResource) {
        return projectService.createProjectsFromFundingDecisions(fundingNotificationResource.getFundingDecisions())
                .andOnSuccess(() -> applicationFundingService.notifyLeadApplicantsOfFundingDecisions(fundingNotificationResource))
            .toPostResponse();
    }
    
    @PostMapping(value="/{competitionId}")
    public RestResult<Void> saveFundingDecisionData(@PathVariable("competitionId") final Long competitionId, @RequestBody Map<Long, FundingDecision> applicationFundingDecisions) {
        return applicationFundingService.saveFundingDecisionData(competitionId, applicationFundingDecisions).
                toPutResponse();
    }
}
