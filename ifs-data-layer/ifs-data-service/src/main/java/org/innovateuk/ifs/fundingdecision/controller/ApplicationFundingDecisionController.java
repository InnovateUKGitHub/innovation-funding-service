package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
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
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @PostMapping(value="/send-notifications")
    public RestResult<Void> sendFundingDecisions(@RequestBody FundingNotificationResource fundingNotificationResource) {
        if (isReleaseFeedbackCompletionStage(fundingNotificationResource.getFundingDecisions())) {
            return applicationFundingService.notifyApplicantsOfFundingDecisions(fundingNotificationResource)
                    .toPostResponse();
        } else {
            return projectService.createProjectsFromFundingDecisions(fundingNotificationResource.getFundingDecisions())
                    .andOnSuccess(() -> applicationFundingService.notifyApplicantsOfFundingDecisions(fundingNotificationResource))
                    .toPostResponse();
        }
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

    private boolean isReleaseFeedbackCompletionStage(Map<Long, FundingDecision> fundingDecisions) {
        return fundingDecisions.keySet().stream().findFirst().map(applicationId -> {
            ApplicationResource application = applicationService.getApplicationById(applicationId).getSuccess();
            CompetitionResource competition = competitionService.getCompetitionById(application.getCompetition()).getSuccess();
            return CompetitionCompletionStage.RELEASE_FEEDBACK.equals(competition.getCompletionStage());
        }).orElse(false);
    }
}
