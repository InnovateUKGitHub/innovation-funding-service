package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingNotificationBulkService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Responsible for receiving calls to set the funding decision for all applications for a given competition.
 */
@RestController
@RequestMapping("/applicationfunding")
public class ApplicationDecisionController {

    @Autowired
    private ApplicationFundingService applicationFundingService;

    @Autowired
    private ApplicationFundingNotificationBulkService applicationFundingNotificationBulkService;

    @PostMapping(value="/send-notifications")
    public RestResult<Void> sendDecisions(@RequestBody FundingNotificationResource fundingNotificationResource) {
        return applicationFundingNotificationBulkService.sendBulkFundingNotifications(fundingNotificationResource).toPostResponse();
    }
    
    @PostMapping(value="/{competitionId}")
    public RestResult<Void> saveDecisionData(@PathVariable("competitionId") final Long competitionId, @RequestBody Map<Long, Decision> applicationDecisions) {
        return applicationFundingService.saveDecisionData(competitionId, applicationDecisions).
                toPutResponse();
    }

    @GetMapping("/notifications-to-send")
    public RestResult<List<ApplicationDecisionToSendApplicationResource>> getNotificationResourceForApplications(@RequestParam("applicationIds") List<Long> applicationIds) {
        return applicationFundingService.getNotificationResourceForApplications(applicationIds).toGetResponse();
    }

}
