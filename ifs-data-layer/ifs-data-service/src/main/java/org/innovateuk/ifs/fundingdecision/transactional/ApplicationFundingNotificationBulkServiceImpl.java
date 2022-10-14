package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationEoiService applicationEoiService;

    @Override
    public ServiceResult<Void> sendBulkFundingNotifications(FundingNotificationResource fundingNotificationResource) {
        if (!fundingNotificationTriggersProjectSetup(fundingNotificationResource.getDecisions())) {
            return notifyWithFullApplicationCreation(fundingNotificationResource);

        } else {
            Map<Long, Decision> successfulDecisions = fundingNotificationResource.getDecisions()
                    .entrySet().stream()
                    .filter(entry -> filterSuccessfulDecisions(entry))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<Long, Decision> otherDecisions = fundingNotificationResource.getDecisions()
                    .entrySet().stream()
                    .filter(entry -> !filterSuccessfulDecisions(entry))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            ServiceResult<Void> result = serviceSuccess();
            if (!otherDecisions.isEmpty()) {
                result.andOnSuccess(() -> applicationFundingService.notifyApplicantsOfDecisions(new FundingNotificationResource(fundingNotificationResource.getMessageBody(), otherDecisions)));
            }
            if (!successfulDecisions.isEmpty()) {
                result.andOnSuccess(() -> handleSuccessfulNotificationsCreatingProjects(new FundingNotificationResource(fundingNotificationResource.getMessageBody(), successfulDecisions)));
            }
            return result;
        }
    }



    private boolean filterSuccessfulDecisions(Map.Entry<Long, Decision> entry) {
        return entry.getValue() == Decision.FUNDED
                || entry.getValue() == Decision.EOI_APPROVED;
    }

    private ServiceResult<Void> handleSuccessfulNotificationsCreatingProjects(FundingNotificationResource fundingNotificationResource) {
        return aggregate(fundingNotificationResource.getDecisions().keySet().stream()
                .map(applicationId -> applicationService.getApplicationById(applicationId).getSuccess())
                .map(application -> application.isEnabledForExpressionOfInterest()
                        ? sendEoiNotificationAndCreateFullApplication(application.getId(), fundingNotificationResource.getMessageBody())
                        : projectToBeCreatedService.markApplicationReadyToBeCreated(application.getId(), fundingNotificationResource.getMessageBody()))
                .collect(toList()))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> sendEoiNotificationAndCreateFullApplication(long applicationId, String emailBody) {
        return applicationFundingService.notifyApplicantsOfDecisions(
                    new FundingNotificationResource(emailBody, Collections.singletonMap(applicationId, Decision.EOI_APPROVED)))
                .andOnSuccessReturnVoid(() -> applicationEoiService.createFullApplicationFromEoi(applicationId));
    }

    private boolean fundingNotificationTriggersProjectSetup(Map<Long, Decision> decisions) {
        return decisions.keySet().stream().findFirst().map(applicationId -> {
            CompetitionResource competition = competitionService.getCompetitionByApplicationId(applicationId).getSuccess();
            return CompetitionCompletionStage.PROJECT_SETUP.equals(competition.getCompletionStage())  && !competition.isKtp();
        }).orElse(false);
    }

    private ServiceResult<Void> notifyWithFullApplicationCreation(FundingNotificationResource fundingNotificationResource) {
        return aggregate(fundingNotificationResource.getDecisions().keySet().stream()
                .map(applicationId -> applicationService.getApplicationById(applicationId).getSuccess())
                .map(application -> application.isEnabledForExpressionOfInterest()
                        ? sendEoiNotificationAndCreateFullApplication(application.getId(), fundingNotificationResource.getMessageBody())
                        : applicationFundingService.notifyApplicantsOfDecisions(fundingNotificationResource))
                .collect(toList()))
                .andOnSuccessReturnVoid();
    }
}
