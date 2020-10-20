package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingNotificationBulkService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationFundingNotificationBulkService applicationFundingNotificationBulkService;

    @PostMapping(value="/send-notifications")
    public RestResult<Void> sendFundingDecisions(@RequestBody FundingNotificationResource fundingNotificationResource) {
        return applicationFundingNotificationBulkService.sendBulkFundingNotifications(fundingNotificationResource).toPostCreateResponse();
    }
    
    @PostMapping(value="/{competitionId}")
    public RestResult<Void> saveFundingDecisionData(@PathVariable("competitionId") final Long competitionId, @RequestBody Map<Long, FundingDecision> applicationFundingDecisions) {
        return applicationFundingService.saveFundingDecisionData(competitionId, applicationFundingDecisions).
                toPutResponse();
    }

    @GetMapping("/notifications-to-send")
    public RestResult<List<FundingDecisionToSendApplicationResource>> getNotificationResourceForApplications(@RequestParam("applicationIds") List<Long> applicationIds) {
        return applicationFundingService.getNotificationResourceForApplications(applicationIds).toGetResponse();
    }

}
