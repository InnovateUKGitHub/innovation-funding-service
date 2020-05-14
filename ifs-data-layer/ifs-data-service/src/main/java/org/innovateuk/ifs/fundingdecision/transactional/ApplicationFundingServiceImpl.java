package org.innovateuk.ifs.fundingdecision.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.fundingdecision.mapper.FundingDecisionMapper;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.util.EntityLookupCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FUNDING_PANEL_DECISION_NONE_PROVIDED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_DETERMINE_NOTIFICATION_TARGETS;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_FUNDING;
import static org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingServiceImpl.Notifications.HORIZON_2020_FUNDING;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.pairsToMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private FundingDecisionMapper fundingDecisionMapper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public enum Notifications {
        APPLICATION_FUNDING, HORIZON_2020_FUNDING
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
        if (applicationFundingDecisions.isEmpty()) {
            return serviceFailure(FUNDING_PANEL_DECISION_NONE_PROVIDED);
        }
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<Application> applications = findValidApplications(applicationFundingDecisions, competitionId);
            return saveFundingDecisionData(applications, applicationFundingDecisions);
        });
    }

    @Override
    public ServiceResult<List<FundingDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds) {
        return serviceSuccess(StreamSupport.stream( applicationRepository.findAllById(applicationIds).spliterator(), false)
                .map(application -> {
                    Organisation organisation = organisationRepository.findById(application.getLeadOrganisationId()).get();
                    return new FundingDecisionToSendApplicationResource(application.getId(), application.getName(), organisation.getName(), FundingDecision.valueOf(application.getFundingDecision().name()));
                }).collect(toList()));

    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyApplicantsOfFundingDecisions(FundingNotificationResource fundingNotificationResource) {

        List<Application> applications = getFundingApplications(fundingNotificationResource.getFundingDecisions());
        setApplicationState(fundingNotificationResource.getFundingDecisions(), applications);

        List<ServiceResult<Pair<Long, NotificationTarget>>> fundingNotificationTargets = getApplicantNotificationTargets(fundingNotificationResource.calculateApplicationIds());
        ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedFundingTargets = aggregate(fundingNotificationTargets);

        return aggregatedFundingTargets.handleSuccessOrFailure(
                failure -> serviceFailure(NOTIFICATIONS_UNABLE_TO_DETERMINE_NOTIFICATION_TARGETS),
                success -> {

                    Notification fundingNotification = createFundingDecisionNotification(applications, fundingNotificationResource, aggregatedFundingTargets.getSuccess());
                    ServiceResult<Void> fundedEmailSendResult = notificationService.sendNotificationWithFlush(fundingNotification, EMAIL);

                    ServiceResult<Void> setEmailDateTimeResult = fundedEmailSendResult.andOnSuccess(() ->
                            aggregate(simpleMap(
                                    applications, application ->
                                            applicationService.setApplicationFundingEmailDateTime(application.getId(), ZonedDateTime.now()))))
                            .andOnSuccessReturnVoid();
                    return setEmailDateTimeResult.andOnSuccess(() -> {
                        if (!applications.isEmpty()) {
                            return competitionService.manageInformState(
                                    applications.get(0)
                                            .getCompetition()
                                            .getId());
                        }
                        return serviceSuccess();
                    });
                });
    }

    private List<Application> getFundingApplications(Map<Long, FundingDecision> applicationFundingDecisions) {

        List<Long> applicationIds = new ArrayList<>(applicationFundingDecisions.keySet());
        return (List) applicationRepository.findAllById(applicationIds);
    }

    private void setApplicationState(Map<Long, FundingDecision> applicationFundingDecisions, List<Application> applications) {

        applications.forEach(app -> {
            FundingDecision applicationFundingDecision = applicationFundingDecisions.get(app.getId());
            ApplicationState state = stateFromDecision(applicationFundingDecision);
            applicationWorkflowHandler.notifyFromApplicationState(app, state);
        });
    }

    private List<Application> findValidApplications(Map<Long, FundingDecision> applicationFundingDecisions, long competitionId) {
        return applicationRepository.findAllowedApplicationsForCompetition(applicationFundingDecisions.keySet(), competitionId);
    }

    private ServiceResult<Void> saveFundingDecisionData(List<Application> applicationsForCompetition, Map<Long, FundingDecision> applicationDecisions) {
        applicationDecisions.forEach((applicationId, decisionValue) -> {
            Optional<Application> applicationForDecision = applicationsForCompetition.stream().filter(application -> applicationId.equals(application.getId())).findAny();
            if (applicationForDecision.isPresent()) {
                Application application = applicationForDecision.get();
                FundingDecisionStatus fundingDecision = fundingDecisionMapper.mapToDomain(decisionValue);
                resetNotificationSentDateIfNecessary(application, fundingDecision);
                application.setFundingDecision(fundingDecision);
                updateApplicationWorkflowImmediatelyIfCompetitionIsInProjectSetup(application, fundingDecision);
            }
        });

        return serviceSuccess();
    }

    private void updateApplicationWorkflowImmediatelyIfCompetitionIsInProjectSetup(Application application, FundingDecisionStatus fundingDecision) {
        if (FundingDecisionStatus.FUNDED.equals(fundingDecision) &&
                application.getCompetition().inProjectSetup()) {
            applicationWorkflowHandler.approve(application);
        }
    }

    private void resetNotificationSentDateIfNecessary(Application application, FundingDecisionStatus newFundingDecision) {
        if (fundingDecisionHasChanged(application, newFundingDecision)) {
            resetNotificationEmailSentDate(application);
        }
    }

    private boolean fundingDecisionHasChanged(Application application, FundingDecisionStatus newFundingDecision) {

        Optional<FundingDecisionStatus> oldFundingDecision = Optional.ofNullable(application.getFundingDecision());
        return oldFundingDecision.map(decision -> !decision.equals(newFundingDecision))
                .orElse(false);
    }

    private void resetNotificationEmailSentDate(Application application) {
        applicationService.setApplicationFundingEmailDateTime(application.getId(), null);
    }

    private Notification createFundingDecisionNotification(
            List<Application> applications,
            FundingNotificationResource fundingNotificationResource,
            List<Pair<Long, NotificationTarget>> notificationTargetsByApplicationId
    ) {
        Competition competition = applications.get(0)
                        .getCompetition();
        boolean includeAsesssorScore = Boolean.TRUE.equals(competition.getCompetitionAssessmentConfig().getIncludeAverageAssessorScoreInNotifications());
        Notifications notificationType = isH2020Competition(applications) ? HORIZON_2020_FUNDING : APPLICATION_FUNDING;
        Map<String, Object> globalArguments = new HashMap<>();

        List<Pair<NotificationTarget, Map<String, Object>>> notificationTargetSpecificArgumentList = simpleMap(
                notificationTargetsByApplicationId,
                pair -> {
                    Long applicationId = pair.getKey();
                    Application application = applications.stream().filter(x -> x.getId().equals(applicationId)).findFirst().get();

                    Map<String, Object> perNotificationTargetArguments = new HashMap<>();
                    perNotificationTargetArguments.put("applicationName", application.getName());
                    perNotificationTargetArguments.put("applicationId", applicationId);
                    perNotificationTargetArguments.put("competitionName", application.getCompetition().getName());
                    if (includeAsesssorScore) {
                        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(applicationId);
                        averageAssessorScore.ifPresent(score -> perNotificationTargetArguments.put("averageAssessorScore", "Average assessor score: " + score.getScore() + "%"));
                    }

                    return Pair.of(pair.getValue(), perNotificationTargetArguments);
                });
        globalArguments.put("message", fundingNotificationResource.getMessageBody());

        List<NotificationTarget> notificationTargets = simpleMap(notificationTargetsByApplicationId, Pair::getValue);

        Map<NotificationTarget, Map<String, Object>> notificationTargetSpecificArguments = pairsToMap(notificationTargetSpecificArgumentList);
        return new Notification(systemNotificationSource, notificationTargets, notificationType, globalArguments, notificationTargetSpecificArguments);
    }

    private List<ServiceResult<Pair<Long, NotificationTarget>>> getApplicantNotificationTargets(List<Long> applicationIds) {

        List<ServiceResult<Pair<Long, NotificationTarget>>> applicationNotificationTargets = new ArrayList<>();
        applicationIds.forEach(applicationId -> {
            ServiceResult<List<ProcessRole>> processRoles = getProcessRoles(applicationId, COLLABORATOR);
            if(processRoles.isSuccess()) {
                processRoles.getSuccess().forEach(pr -> applicationNotificationTargets.add(ServiceResult.serviceSuccess(Pair.of(applicationId, new UserNotificationTarget(pr.getUser().getName(), pr.getUser().getEmail())))));
            }
            applicationNotificationTargets.add(getProcessRoles(applicationId, LEADAPPLICANT).andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail).andOnSuccessReturn(pr -> Pair.of(applicationId, new UserNotificationTarget(pr.getUser().getName(), pr.getUser().getEmail()))));
        });
        return applicationNotificationTargets;
    }

    private boolean isH2020Competition(List<Application> applications) {
        return applications.get(0).getCompetition().isH2020();
    }

    private ApplicationState stateFromDecision(FundingDecision applicationFundingDecision) {
        if (FUNDED.equals(applicationFundingDecision)) {
            return ApplicationState.APPROVED;
        } else if (UNFUNDED.equals(applicationFundingDecision)) {
            return ApplicationState.REJECTED;
        } else {
            return ApplicationState.SUBMITTED;
        }
    }
}
