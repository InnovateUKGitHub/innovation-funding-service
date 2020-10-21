package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationFundingNotificationBulkServiceImpl implements ApplicationFundingNotificationBulkService {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationFundingService applicationFundingService;

    @Autowired
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Override
    public ServiceResult<Void> sendBulkFundingNotifications(FundingNotificationResource fundingNotificationResource) {
        if (isReleaseFeedbackCompletionStage(fundingNotificationResource.getFundingDecisions())) {
            return applicationFundingService.notifyApplicantsOfFundingDecisions(fundingNotificationResource);
        } else {
            Map<Long, FundingDecision> successfulDecisions = fundingNotificationResource.getFundingDecisions()
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() == FundingDecision.FUNDED)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<Long, FundingDecision> otherDecisions = fundingNotificationResource.getFundingDecisions()
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() != FundingDecision.FUNDED)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            ServiceResult<Void> result = serviceSuccess();
            if (!otherDecisions.isEmpty()) {
                result.andOnSuccess(() -> applicationFundingService.notifyApplicantsOfFundingDecisions(new FundingNotificationResource(fundingNotificationResource.getMessageBody(), otherDecisions)));
            }
            if (!successfulDecisions.isEmpty()) {
                result.andOnSuccess(() -> handleSuccessfulNotificationsCreatingProjects(new FundingNotificationResource(fundingNotificationResource.getMessageBody(), successfulDecisions)));
            }
            return result;
        }
    }

    private ServiceResult<Void> handleSuccessfulNotificationsCreatingProjects(FundingNotificationResource fundingNotificationResource) {
        return aggregate(fundingNotificationResource.getFundingDecisions().keySet().stream()
                .map(applicationId -> projectToBeCreatedService.markApplicationReadyToBeCreated(applicationId, fundingNotificationResource.getMessageBody()))
                .collect(toList()))
                .andOnSuccessReturnVoid();
    }

    private boolean isReleaseFeedbackCompletionStage(Map<Long, FundingDecision> fundingDecisions) {
        return fundingDecisions.keySet().stream().findFirst().map(applicationId -> {
            CompetitionResource competition = competitionService.getCompetitionByApplicationId(applicationId).getSuccess();
            return CompetitionCompletionStage.RELEASE_FEEDBACK.equals(competition.getCompletionStage());
        }).orElse(false);
    }

}
