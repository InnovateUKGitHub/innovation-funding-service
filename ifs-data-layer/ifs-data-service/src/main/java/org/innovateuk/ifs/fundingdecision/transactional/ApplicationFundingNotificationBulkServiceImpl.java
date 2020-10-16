package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.project.core.transactional.ProjectCreationAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ProjectCreationAsyncService projectCreationAsyncService;

    @Override
    public ServiceResult<Void> doit(FundingNotificationResource fundingNotificationResource) {
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
        List<ServiceResult<Void>> results = fundingNotificationResource.getFundingDecisions().entrySet().stream().map(entry ->
            applicationFundingService.markApplicationAsNotified(entry.getKey())
                .andOnSuccess(() -> projectCreationAsyncService.createAsync(id ->
                        applicationFundingService.notifyApplicantsOfFundingDecisions(new FundingNotificationResource(fundingNotificationResource.getMessageBody(), singleMap(id, FundingDecision.FUNDED))),
                        entry.getKey(),
                        id -> applicationFundingService.markApplicationAsUnNotified(id)
                )))
                .collect(Collectors.toList());
        return aggregate(results).andOnSuccessReturnVoid();
    }

    private Map<Long, FundingDecision> singleMap(long applicationId, FundingDecision decision) {
        Map<Long, FundingDecision> map = new HashMap<>();
        map.put(applicationId, decision);
        return map;
    }

    private boolean isReleaseFeedbackCompletionStage(Map<Long, FundingDecision> fundingDecisions) {
        return fundingDecisions.keySet().stream().findFirst().map(applicationId -> {
            CompetitionResource competition = competitionService.getCompetitionByApplicationId(applicationId).getSuccess();
            return CompetitionCompletionStage.RELEASE_FEEDBACK.equals(competition.getCompletionStage());
        }).orElse(false);
    }

}
