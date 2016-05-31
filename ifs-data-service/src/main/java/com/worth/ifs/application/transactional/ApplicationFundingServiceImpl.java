package com.worth.ifs.application.transactional;

import static com.worth.ifs.application.constant.ApplicationStatusConstants.APPROVED;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.REJECTED;
import static com.worth.ifs.application.resource.FundingDecision.FUNDED;
import static com.worth.ifs.application.resource.FundingDecision.UNFUNDED;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_FUNDED;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_NOT_FUNDED;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_NOT_ALL_APPLICATIONS_REPRESENTED;
import static com.worth.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_NO_ASSESSOR_FEEDBACK_DATE_SET;
import static com.worth.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_WRONG_STATUS;
import static com.worth.ifs.commons.service.ServiceResult.aggregate;
import static com.worth.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.pairsToMap;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.toListOfPairs;
import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.FundingDecisionStatus;
import com.worth.ifs.application.mapper.FundingDecisionMapper;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.util.EntityLookupCallbacks;

@Service
class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SystemNotificationSource systemNotificationSource;
	
	@Autowired
	private FundingDecisionMapper fundingDecisionMapper;

	@Value("${ifs.web.baseURL}")
	private String webBaseUrl;

	enum Notifications {
		APPLICATION_FUNDED,
		APPLICATION_NOT_FUNDED,
    }

    private static final Log LOG = LogFactory.getLog(ApplicationFundingServiceImpl.class);

	@Override
	public ServiceResult<Void> saveFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
		return getCompetition(competitionId).andOnSuccess(competition -> {
			List<Application> applicationsForCompetition = findSubmittedApplicationsForCompetition(competitionId);

		    return saveFundingDecisionData(competition, applicationsForCompetition, applicationFundingDecisions);
		 });
	}
	
	@Override
	public ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
		return getCompetition(competitionId).andOnSuccess(competition -> {

			if (competition.getAssessorFeedbackDate() == null) {
				LOG.error("cannot make funding decision for a competition without an assessor feedback date set: " + competitionId);
				return serviceFailure(FUNDING_PANEL_DECISION_NO_ASSESSOR_FEEDBACK_DATE_SET);
			}
			
			if(!CompetitionResource.Status.FUNDERS_PANEL.equals(competition.getCompetitionStatus())){
				LOG.error("cannot make funding decision for a competition not in FUNDERS_PANEL status: " + competitionId);
				return serviceFailure(FUNDING_PANEL_DECISION_WRONG_STATUS);
			}
			
			List<Application> applicationsForCompetition = findSubmittedApplicationsForCompetition(competitionId);
			
			saveFundingDecisionData(competition, applicationsForCompetition, applicationFundingDecisions);
			
			boolean allPresent = applicationsForCompetition.stream().noneMatch(app -> !applicationFundingDecisions.containsKey(app.getId()) || FundingDecision.UNDECIDED.equals(applicationFundingDecisions.get(app.getId())));

			if(!allPresent) {
				return serviceFailure(FUNDING_PANEL_DECISION_NOT_ALL_APPLICATIONS_REPRESENTED);
			}

			applicationsForCompetition.forEach(app -> {
				FundingDecision applicationFundingDecision = applicationFundingDecisions.get(app.getId());
				ApplicationStatus status = statusFromDecision(applicationFundingDecision);
				app.setApplicationStatus(status);
			});

			competition.setFundersPanelEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			
			return serviceSuccess();
		});
	}

	@Override
	public ServiceResult<Void> notifyLeadApplicantsOfFundingDecisions(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {

		List<Pair<Long, FundingDecision>> decisions = toListOfPairs(applicationFundingDecisions);
		List<Pair<Long, FundingDecision>> fundedApplicationDecisions = simpleFilter(decisions, decision -> FUNDED.equals(decision.getValue()));
		List<Pair<Long, FundingDecision>> unfundedApplicationDecisions = simpleFilter(decisions, decision -> UNFUNDED.equals(decision.getValue()));
        List<Long> fundedApplicationIds = simpleMap(fundedApplicationDecisions, Pair::getKey);
        List<Long> unfundedApplicationIds = simpleMap(unfundedApplicationDecisions, Pair::getKey);

		List<ServiceResult<Pair<Long, NotificationTarget>>> fundedNotificationTargets = getLeadApplicantNotificationSources(fundedApplicationIds);
        List<ServiceResult<Pair<Long, NotificationTarget>>> unfundedNotificationTargets = getLeadApplicantNotificationSources(unfundedApplicationIds);

        ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedFundedTargets = aggregate(fundedNotificationTargets);
        ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedUnfundedTargets = aggregate(unfundedNotificationTargets);

        if (aggregatedFundedTargets.isSuccess() && aggregatedUnfundedTargets.isSuccess()) {

            ServiceResult<Notification> fundedNotification = createFundingDecisionNotification(competitionId, aggregatedFundedTargets.getSuccessObject(), APPLICATION_FUNDED);
            ServiceResult<Void> fundedEmailSendResult = fundedNotification.andOnSuccessReturnVoid(
                    notification -> notificationService.sendNotification(notification, EMAIL));

            ServiceResult<Notification> unfundedNotification = createFundingDecisionNotification(competitionId, aggregatedUnfundedTargets.getSuccessObject(), APPLICATION_NOT_FUNDED);
            ServiceResult<Void> unfundedEmailSendResult = unfundedNotification.andOnSuccessReturnVoid(
                    notification -> notificationService.sendNotification(notification, EMAIL));

            List<ServiceResult<Void>> allEmailResults = asList(fundedEmailSendResult, unfundedEmailSendResult);
            return processAnyFailuresOrSucceed(allEmailResults, serviceSuccess());
        } else {
            return serviceFailure(internalServerErrorError("Unable to determine all Notification targets for funding decision emails"));
        }
	}

	private List<Application> findSubmittedApplicationsForCompetition(Long competitionId) {
		return applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.SUBMITTED.getId());
	}
	
	private ServiceResult<Void> saveFundingDecisionData(Competition competition, List<Application> applicationsForCompetition, Map<Long, FundingDecision> decision) {
		decision.forEach((applicationId, decisionValue) -> {
			Optional<Application> applicationForDecision = applicationsForCompetition.stream().filter(application -> applicationId.equals(application.getId())).findFirst();
			if(applicationForDecision.isPresent()) {
				Application application = applicationForDecision.get();
				FundingDecisionStatus fundingDecision = fundingDecisionMapper.mapToDomain(decisionValue);
				application.setFundingDecision(fundingDecision);
			}
		});
		 
		return serviceSuccess();
	}

	private ServiceResult<Notification> createFundingDecisionNotification(Long competitionId, List<Pair<Long, NotificationTarget>> notificationTargetsByApplicationId, Notifications notificationType) {

        return getCompetition(competitionId).andOnSuccessReturn(competition -> {

            Map<String, Object> globalArguments = new HashMap<>();
            globalArguments.put("competitionName", competition.getName());
            globalArguments.put("dashboardUrl", webBaseUrl);
            globalArguments.put("feedbackDate", competition.getAssessorFeedbackDate());

            List<Pair<NotificationTarget, Map<String, Object>>> notificationTargetSpecificArgumentList = simpleMap(notificationTargetsByApplicationId, pair -> {

                Long applicationId = pair.getKey();
                Application application = applicationRepository.findOne(applicationId);

                Map<String, Object> perNotificationTargetArguments = new HashMap<>();
                perNotificationTargetArguments.put("applicationName", application.getName());
                return Pair.of(pair.getValue(), perNotificationTargetArguments);
            });

            List<NotificationTarget> notificationTargets = simpleMap(notificationTargetsByApplicationId, Pair::getValue);
            Map<NotificationTarget, Map<String, Object>> notificationTargetSpecificArguments = pairsToMap(notificationTargetSpecificArgumentList);
            return new Notification(systemNotificationSource, notificationTargets, notificationType, globalArguments, notificationTargetSpecificArguments);
        });
	}

	private List<ServiceResult<Pair<Long, NotificationTarget>>> getLeadApplicantNotificationSources(List<Long> applicationIds) {
		return simpleMap(applicationIds, applicationId -> {
			ServiceResult<ProcessRole> leadApplicantResult = getProcessRoles(applicationId, LEADAPPLICANT).andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail);
			return leadApplicantResult.andOnSuccessReturn(leadApplicant -> Pair.of(applicationId, new UserNotificationTarget(leadApplicant.getUser())));
		});
	}

	private ApplicationStatus statusFromDecision(FundingDecision applicationFundingDecision) {
		if(FUNDED.equals(applicationFundingDecision)) {
			return applicationStatusRepository.findOne(APPROVED.getId());
		} else {
			return applicationStatusRepository.findOne(REJECTED.getId());
		}
	}

	protected void setFundingDecisionMapper(FundingDecisionMapper fundingDecisionMapper) {
		this.fundingDecisionMapper = fundingDecisionMapper;
	}
}
