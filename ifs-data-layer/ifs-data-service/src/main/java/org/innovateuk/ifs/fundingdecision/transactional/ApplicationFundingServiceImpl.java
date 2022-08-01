package org.innovateuk.ifs.fundingdecision.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.innovateuk.ifs.fundingdecision.mapper.DecisionMapper;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.resource.Decision.FUNDED;
import static org.innovateuk.ifs.application.resource.Decision.UNFUNDED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingServiceImpl.Notifications.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private DecisionMapper decisionMapper;

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

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public enum Notifications {
        APPLICATION_FUNDING, HORIZON_2020_FUNDING, HORIZON_EUROPE_FUNDING
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveDecisionData(Long competitionId, Map<Long, Decision> applicationDecisions) {
        if (applicationDecisions.isEmpty()) {
            return serviceFailure(FUNDING_PANEL_DECISION_NONE_PROVIDED);
        }
        return getCompetition(competitionId).andOnSuccess(competition -> {
            List<Application> applications = findValidApplications(applicationDecisions, competitionId);
            return saveDecisionData(applications, applicationDecisions);
        });
    }

    @Override
    public ServiceResult<List<ApplicationDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds) {
        return serviceSuccess(StreamSupport.stream(applicationRepository.findAllById(applicationIds).spliterator(), false)
                .map(application -> {
                    Organisation organisation = organisationRepository.findById(application.getLeadOrganisationId()).get();
                    return new ApplicationDecisionToSendApplicationResource(application.getId(), application.getName(), organisation.getName(), Decision.valueOf(application.getDecision().name()));
                }).collect(toList()));

    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyApplicantsOfDecisions(FundingNotificationResource fundingNotificationResource) {

        List<Application> applications = getFundingApplications(fundingNotificationResource.getDecisions());
        ServiceResult<Void> result = setApplicationState(fundingNotificationResource.getDecisions(), applications);
        if (!applications.isEmpty() && applications.get(0).getCompetition().isKtp()) {
            result = result.andOnSuccess(() -> setKtpFundingState(applications, fundingNotificationResource.getDecisions()));
        }
        return result.andOnSuccess(() -> {
            List<ServiceResult<Pair<Long, NotificationTarget>>> fundingNotificationTargets;
            if (!applications.isEmpty() && applications.get(0).getCompetition().isKtp()) {
                fundingNotificationTargets = getKtpApplicantNotificationTargets(applications);
            } else {
                fundingNotificationTargets = getApplicantNotificationTargets(fundingNotificationResource.calculateApplicationIds());
            }
            ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedFundingTargets = aggregate(fundingNotificationTargets);

            return aggregatedFundingTargets.handleSuccessOrFailure(
                    failure -> serviceFailure(NOTIFICATIONS_UNABLE_TO_DETERMINE_NOTIFICATION_TARGETS),
                    success -> {

                        Notification fundingNotification = createDecisionNotification(applications, fundingNotificationResource, aggregatedFundingTargets.getSuccess());
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
        });
    }

    private List<ServiceResult<Pair<Long, NotificationTarget>>> getKtpApplicantNotificationTargets(List<Application> applications) {
        return applications.stream().map(application -> {
            List<NotificationTarget> targets = application.getProject().getProjectUsers().stream()
                    .filter(pu -> pu.getRole() == ProjectParticipantRole.PROJECT_PARTNER)
                    .map(pu -> new UserNotificationTarget(pu.getUser().getName(), pu.getUser().getEmail()))
                    .collect(toList());
            application.getProject().getProjectMonitoringOfficer().ifPresent(mo -> targets.add(new UserNotificationTarget(mo.getUser().getName(), mo.getUser().getEmail())));
            return targets.stream().map(target -> Pair.of(application.getId(), target)).collect(toList());
        })
                .flatMap(List::stream)
                .map(ServiceResult::serviceSuccess)
                .collect(toList());
    }

    private ServiceResult<Void> setKtpFundingState(List<Application> applications, Map<Long, Decision> decisions) {
        return aggregate(applications.stream().map(application -> {
            if (application.getProject() == null) {
                return serviceFailure(new Error(FUNDING_DECISION_KTP_PROJECT_NOT_YET_CREATED, application.getId())).andOnSuccessReturnVoid();
            }
            if (decisions.get(application.getId()) == UNFUNDED) {
                return getCurrentlyLoggedInUser().andOnSuccess(user -> {
                    if (projectWorkflowHandler.markAsUnsuccessful(application.getProject(), user)) {
                        return serviceSuccess();
                    }
                    return serviceFailure(PROJECT_SETUP_CANNOT_PROGRESS_WORKFLOW).andOnSuccessReturnVoid();
                });
            }
            return serviceSuccess();
        }).collect(toList()))
                .andOnSuccessReturnVoid();
    }

    private List<Application> getFundingApplications(Map<Long, Decision> applicationDecisions) {
        List<Long> applicationIds = new ArrayList<>(applicationDecisions.keySet());
        return newArrayList(applicationRepository.findAllById(applicationIds));
    }

    private ServiceResult<Void> setApplicationState(Map<Long, Decision> applicationDecisions, List<Application> applications) {
        return aggregate(applications.stream().map(app -> {
            Decision applicationDecision = applicationDecisions.get(app.getId());
            ApplicationState state = stateFromDecision(applicationDecision);
            if (state == app.getApplicationProcess().getProcessState() || applicationWorkflowHandler.notifyFromApplicationState(app, state)) {
                return serviceSuccess();
            }
            return serviceFailure(FUNDING_DECISION_WORKFLOW_FAILURE).andOnSuccessReturnVoid();
        }).collect(toList()))
                .andOnSuccessReturnVoid();
    }

    private List<Application> findValidApplications(Map<Long, Decision> applicationDecisions, long competitionId) {
        return applicationRepository.findAllowedApplicationsForCompetition(applicationDecisions.keySet(), competitionId);
    }

    private ServiceResult<Void> saveDecisionData(List<Application> applicationsForCompetition, Map<Long, Decision> applicationDecisions) {
        applicationDecisions.forEach((applicationId, decisionValue) -> {
            Optional<Application> applicationForDecision = applicationsForCompetition.stream().filter(application -> applicationId.equals(application.getId())).findAny();
            if (applicationForDecision.isPresent()) {
                Application application = applicationForDecision.get();
                DecisionStatus decision = decisionMapper.mapToDomain(decisionValue);
                resetNotificationSentDateIfNecessary(application, decision);
                application.setDecision(decision);
                updateApplicationWorkflowImmediatelyIfCompetitionIsInProjectSetup(application, decision);
            }
        });

        return serviceSuccess();
    }

    private void updateApplicationWorkflowImmediatelyIfCompetitionIsInProjectSetup(Application application, DecisionStatus decision) {
        if (DecisionStatus.FUNDED.equals(decision) &&
                application.getCompetition().inProjectSetup()) {
            applicationWorkflowHandler.approve(application);
        }
    }

    private void resetNotificationSentDateIfNecessary(Application application, DecisionStatus newDecision) {
        if (decisionHasChanged(application, newDecision)) {
            resetNotificationEmailSentDate(application);
        }
    }

    private boolean decisionHasChanged(Application application, DecisionStatus newDecision) {

        Optional<DecisionStatus> oldDecision = Optional.ofNullable(application.getDecision());
        return oldDecision.map(decision -> !decision.equals(newDecision))
                .orElse(false);
    }

    private void resetNotificationEmailSentDate(Application application) {
        applicationService.setApplicationFundingEmailDateTime(application.getId(), null);
    }

    private Notification createDecisionNotification(
            List<Application> applications,
            FundingNotificationResource fundingNotificationResource,
            List<Pair<Long, NotificationTarget>> notificationTargetsByApplicationId
    ) {
        Competition competition = applications.get(0)
                .getCompetition();
        boolean includeAssesssorScore = Boolean.TRUE.equals(competition.getCompetitionAssessmentConfig().getIncludeAverageAssessorScoreInNotifications());
        Notifications notificationType = getNotificationType(applications, competition);
        Map<String, Object> globalArguments = new HashMap<>();

        List<NotificationMessage> notificationMessages = simpleMap(
                notificationTargetsByApplicationId,
                pair -> {
                    Long applicationId = pair.getKey();
                    Application application = applications.stream().filter(x -> x.getId().equals(applicationId)).findFirst().get();

                    Map<String, Object> perNotificationTargetArguments = new HashMap<>();
                    perNotificationTargetArguments.put("applicationName", application.getName());
                    perNotificationTargetArguments.put("applicationId", applicationId);
                    perNotificationTargetArguments.put("competitionName", application.getCompetition().getName());
                    perNotificationTargetArguments.put("competitionId", application.getCompetition().getId());
                    perNotificationTargetArguments.put("alwaysOpen", application.getCompetition().isAlwaysOpen());
                    perNotificationTargetArguments.put("directAward", application.getCompetition().isDirectAward());
                    perNotificationTargetArguments.put("webBaseUrl", webBaseUrl);
                    if (includeAssesssorScore) {
                        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(applicationId);
                        averageAssessorScore.ifPresent(score -> perNotificationTargetArguments.put("averageAssessorScore", "Average assessor score: " + score.getScore() + "%"));
                    }

                    return new NotificationMessage(pair.getValue(), perNotificationTargetArguments);
                });
        globalArguments.put("message", fundingNotificationResource.getMessageBody());

        return new Notification(systemNotificationSource, notificationMessages, notificationType, globalArguments);
    }

    private Notifications getNotificationType(List<Application> applications, Competition competition) {
        Notifications notificationType;

        if(isH2020Competition(applications)){
            notificationType = HORIZON_2020_FUNDING;
        } else if (competition.isHorizonEuropeGuarantee()){
            notificationType = HORIZON_EUROPE_FUNDING;
        } else {
            notificationType = APPLICATION_FUNDING;
        }

        return notificationType;
    }

    private List<ServiceResult<Pair<Long, NotificationTarget>>> getApplicantNotificationTargets(List<Long> applicationIds) {

        List<ServiceResult<Pair<Long, NotificationTarget>>> applicationNotificationTargets = new ArrayList<>();
        applicationIds.forEach(applicationId -> {
            ServiceResult<List<ProcessRole>> processRoles = getProcessRoles(applicationId, COLLABORATOR);
            if (processRoles.isSuccess()) {
                processRoles.getSuccess()
                        .stream()
                        .filter(pr -> pr.getUser().isActive())
                        .forEach(pr -> applicationNotificationTargets.add(ServiceResult.serviceSuccess(Pair.of(applicationId, new UserNotificationTarget(pr.getUser().getName(), pr.getUser().getEmail())))));
            }
            applicationNotificationTargets.add(getProcessRoles(applicationId, LEADAPPLICANT).andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail).andOnSuccessReturn(pr -> Pair.of(applicationId, new UserNotificationTarget(pr.getUser().getName(), pr.getUser().getEmail()))));
        });
        return applicationNotificationTargets;
    }

    private boolean isH2020Competition(List<Application> applications) {
        return applications.get(0).getCompetition().isH2020();
    }

    private ApplicationState stateFromDecision(Decision applicationDecision) {
        switch(applicationDecision) {
            case FUNDED:
            case EOI_APPROVED:
                return ApplicationState.APPROVED;
            case UNFUNDED:
            case EOI_REJECTED:
                return ApplicationState.REJECTED;
            default:
                return ApplicationState.SUBMITTED;
        }
    }
}
