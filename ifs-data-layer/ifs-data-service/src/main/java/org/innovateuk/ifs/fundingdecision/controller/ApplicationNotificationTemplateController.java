package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationNotificationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsible for receiving calls to set the funding decision for all applications for a given competition.
 */
@RestController
@RequestMapping("/application-notification-template")
public class ApplicationNotificationTemplateController {

    @Autowired
    private ApplicationNotificationTemplateService applicationNotificationTemplateService;

    @RequestMapping("/successful/{competitionId}")
    public RestResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(@PathVariable("competitionId") long competitionId) {
        return applicationNotificationTemplateService.getSuccessfulNotificationTemplate(competitionId).toGetResponse();
    }

    @RequestMapping("/unsuccessful/{competitionId}")
    public RestResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(@PathVariable("competitionId") long competitionId) {
        return applicationNotificationTemplateService.getUnsuccessfulNotificationTemplate(competitionId).toGetResponse();
    }

    @RequestMapping("/ineligible/{competitionId}/{userId}")
    public RestResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(@PathVariable("competitionId") long competitionId,
                                                                                                 @PathVariable("userId") long userId) {
        return applicationNotificationTemplateService.getIneligibleNotificationTemplate(competitionId, userId).toGetResponse();
    }
}
