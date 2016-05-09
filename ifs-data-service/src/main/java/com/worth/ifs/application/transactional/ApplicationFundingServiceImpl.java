package com.worth.ifs.application.transactional;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.application.constant.ApplicationStatusConstants.APPROVED;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.REJECTED;
import static com.worth.ifs.application.resource.FundingDecision.FUNDED;
import static com.worth.ifs.application.resource.FundingDecision.NOT_FUNDED;
import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;
import static com.worth.ifs.util.MapFunctions.toListOfPairs;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

@Service
public class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SystemNotificationSource systemNotificationSource;

    private enum Notifications {
        FUNDED_APPLICATION,
        UNFUNDED_APPLICATION,
    }

	@Override
	public ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
		
		List<Application> applicationsForCompetition = applicationRepository.findByCompetitionId(competitionId);
		
		boolean allPresent = applicationsForCompetition.stream().noneMatch(app -> !applicationFundingDecisions.containsKey(app.getId()));
		
		if(!allPresent) {
			return serviceFailure(badRequestError("not all applications represented in funding decision"));
		}
		
		applicationsForCompetition.forEach(app -> {
			FundingDecision applicationFundingDecision = applicationFundingDecisions.get(app.getId());
			ApplicationStatus status = statusFromDecision(applicationFundingDecision);
			app.setApplicationStatus(status);
			applicationRepository.save(app);
		});
		
		return serviceSuccess();
	}

	@Override
	public ServiceResult<Void> notifyLeadApplicantsOfFundingDecisions(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {

		List<Pair<Long, FundingDecision>> decisions = toListOfPairs(applicationFundingDecisions);
		List<Pair<Long, FundingDecision>> fundedApplicationDecisions = simpleFilter(decisions, decision -> FUNDED.equals(decision.getValue()));
		List<Pair<Long, FundingDecision>> unfundedApplicationDecisions = simpleFilter(decisions, decision -> NOT_FUNDED.equals(decision.getValue()));
        List<Long> fundedApplicationIds = simpleMap(fundedApplicationDecisions, Pair::getKey);
        List<Long> unfundedApplicationIds = simpleMap(unfundedApplicationDecisions, Pair::getKey);

		List<ServiceResult<NotificationTarget>> fundedNotificationTargets = getLeadApplicantNotificationSources(fundedApplicationIds);
        List<ServiceResult<NotificationTarget>> unfundedNotificationTargets = getLeadApplicantNotificationSources(unfundedApplicationIds);

        ServiceResult<List<NotificationTarget>> aggregatedFundedTargets = aggregate(fundedNotificationTargets);
        ServiceResult<List<NotificationTarget>> aggregatedUnfundedTargets = aggregate(unfundedNotificationTargets);

        if (aggregatedFundedTargets.isSuccess() && aggregatedUnfundedTargets.isSuccess()) {

            Notification fundedNotification = new Notification(systemNotificationSource, aggregatedFundedTargets.getSuccessObject(), Notifications.FUNDED_APPLICATION, emptyMap());
            ServiceResult<Void> fundedEmailSendResult = notificationService.sendNotification(fundedNotification, EMAIL).andOnSuccessReturnVoid();

            Notification unfundedNotification = new Notification(systemNotificationSource, aggregatedUnfundedTargets.getSuccessObject(), Notifications.UNFUNDED_APPLICATION, emptyMap());
            ServiceResult<Void> unfundedEmailSendResult = notificationService.sendNotification(unfundedNotification, EMAIL).andOnSuccessReturnVoid();

            List<ServiceResult<Void>> allEmailResults = asList(fundedEmailSendResult, unfundedEmailSendResult);
            return processAnyFailuresOrSucceed(allEmailResults, serviceSuccess());
        } else {
            return serviceFailure(internalServerErrorError("Unable to determine all Notification targets for funding decision emails"));
        }
	}

	private List<ServiceResult<NotificationTarget>> getLeadApplicantNotificationSources(List<Long> applicationIds) {
		return simpleMap(applicationIds, applicationId -> {
			ServiceResult<ProcessRole> leadApplicantResult = getProcessRoles(applicationId, LEADAPPLICANT).andOnSuccess(roles -> getOnlyElementOrFail(roles));
			return leadApplicantResult.andOnSuccessReturn(leadApplicant -> new UserNotificationTarget(leadApplicant.getUser()));
		});
	}

	private ApplicationStatus statusFromDecision(FundingDecision applicationFundingDecision) {
		if(FUNDED.equals(applicationFundingDecision)) {
			return statusFromConstant(APPROVED);
		} else {
			return statusFromConstant(REJECTED);
		}
	}

	private ApplicationStatus statusFromConstant(ApplicationStatusConstants applicationStatusConstant) {
		return applicationStatusRepository.findOne(applicationStatusConstant.getId());
	}
	
}
