package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.util.EntityLookupCallbacks;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.FUNDING_DECISIONS_MADE_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static org.innovateuk.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_DETERMINE_NOTIFICATION_TARGETS;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Service
public class AssessorFeedbackServiceImpl extends BaseTransactionalService implements AssessorFeedbackService {

    private static final Predicate<Application> applicationApprovedFilter = application -> application.getApplicationStatus().isApproved();

    enum Notifications {
        APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
        APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getApplication(applicationId).
                andOnSuccess(application -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                andOnSuccessReturn(fileDetails -> linkAssessorFeedbackFileToApplication(application, fileDetails)));
    }

    @Override
    public ServiceResult<FileAndContents> getAssessorFeedbackFileEntryContents(long applicationId) {

        return getApplication(applicationId).andOnSuccess(application -> {

            FileEntry assessorFeedbackFileEntry = application.getAssessorFeedbackFileEntry();

            if (assessorFeedbackFileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(assessorFeedbackFileEntry.getId());
            return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(assessorFeedbackFileEntry), inputStream));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getAssessorFeedbackFileEntryDetails(long applicationId) {

        return getApplication(applicationId).andOnSuccess(application -> {

            FileEntry assessorFeedbackFileEntry = application.getAssessorFeedbackFileEntry();

            if (assessorFeedbackFileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(assessorFeedbackFileEntry));
        });
    }

    @Override
    public ServiceResult<Void> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getApplication(applicationId).
                andOnSuccess(application -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                andOnSuccessReturnVoid(fileDetails -> linkAssessorFeedbackFileToApplication(application, fileDetails)));
    }

    @Override
    public ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId) {
        return getApplication(applicationId).andOnSuccess(application ->
                getAssessorFeedbackFileEntry(application).andOnSuccess(fileEntry ->
                fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid(() ->
                removeAssessorFeedbackFileFromApplication(application))));
    }
    
	@Override
	public ServiceResult<Boolean> assessorFeedbackUploaded(long competitionId) {
		long countNotUploaded = applicationRepository.countByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(competitionId, SUBMITTED_STATUS_IDS);
		boolean allUploaded = countNotUploaded == 0L;
		return serviceSuccess(allUploaded);
	}

	@Override
	public ServiceResult<Void> submitAssessorFeedback(long competitionId) {
		return getCompetition(competitionId).andOnSuccessReturnVoid(competition ->
			competition.setReleaseFeedbackDate(ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
		);
	}

    @Override
    public ServiceResult<Void> notifyLeadApplicantsOfAssessorFeedback(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition -> getCompetitionOnSuccessToNotifyLeadApplicantsOfAssessorFeedback(competition, competitionId));
    }

    private ServiceResult<Void> getCompetitionOnSuccessToNotifyLeadApplicantsOfAssessorFeedback (Competition competition, long competitionId) {
        List<Application> applicationsToPublishAssessorFeedbackFor = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, FUNDING_DECISIONS_MADE_STATUS_IDS);

        Pair<List<Application>, List<Application>> applicationsByFundingSuccess = simplePartition(applicationsToPublishAssessorFeedbackFor, applicationApprovedFilter);
        List<Application> successfulApplications = applicationsByFundingSuccess.getLeft();
        List<Application> unsuccessfulApplications = applicationsByFundingSuccess.getRight();

        List<ServiceResult<Pair<Long, NotificationTarget>>> successfulApplicationTargets = getLeadApplicantNotificationTargets(simpleMap(successfulApplications, Application::getId));
        List<ServiceResult<Pair<Long, NotificationTarget>>> unsuccessfulApplicationTargets = getLeadApplicantNotificationTargets(simpleMap(unsuccessfulApplications, Application::getId));

        ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedFundedTargets = aggregate(successfulApplicationTargets);
        ServiceResult<List<Pair<Long, NotificationTarget>>> aggregatedUnfundedTargets = aggregate(unsuccessfulApplicationTargets);

        if (aggregatedFundedTargets.isSuccess() && aggregatedUnfundedTargets.isSuccess()) {

            Notification fundedNotification = createFundingDecisionNotification(competition, aggregatedFundedTargets.getSuccessObject(), APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED);
            Notification unfundedNotification = createFundingDecisionNotification(competition, aggregatedUnfundedTargets.getSuccessObject(), APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED);

            ServiceResult<Void> fundedEmailSendResult = notificationService.sendNotification(fundedNotification, EMAIL);
            ServiceResult<Void> unfundedEmailSendResult = notificationService.sendNotification(unfundedNotification, EMAIL);

            return processAnyFailuresOrSucceed(fundedEmailSendResult, unfundedEmailSendResult);

        } else {
            return serviceFailure(NOTIFICATIONS_UNABLE_TO_DETERMINE_NOTIFICATION_TARGETS);
        }
    }

    private Notification createFundingDecisionNotification(Competition competition, List<Pair<Long, NotificationTarget>> notificationTargetsByApplicationId, Notifications notificationType) {

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("competitionName", competition.getName());
        globalArguments.put("dashboardUrl", webBaseUrl);
        if (competition.getAssessorFeedbackDate() != null) {
            globalArguments.put("feedbackDate", TimeZoneUtil.toUkTimeZone(competition.getAssessorFeedbackDate()).toLocalDateTime());
        }

        List<Pair<NotificationTarget, Map<String, Object>>> notificationTargetSpecificArgumentList = simpleMap(notificationTargetsByApplicationId, pair -> {

            Long applicationId = pair.getKey();
            Application application = applicationRepository.findOne(applicationId);

            Map<String, Object> perNotificationTargetArguments = new HashMap<>();
            perNotificationTargetArguments.put("applicationName", application.getName());
            perNotificationTargetArguments.put("applicationNumber", application.getId());
            return Pair.of(pair.getValue(), perNotificationTargetArguments);
        });

        List<NotificationTarget> notificationTargets = simpleMap(notificationTargetsByApplicationId, Pair::getValue);
        Map<NotificationTarget, Map<String, Object>> notificationTargetSpecificArguments = pairsToMap(notificationTargetSpecificArgumentList);
        return new Notification(systemNotificationSource, notificationTargets, notificationType, globalArguments, notificationTargetSpecificArguments);
    }

    private List<ServiceResult<Pair<Long, NotificationTarget>>> getLeadApplicantNotificationTargets(List<Long> applicationIds) {
        return simpleMap(applicationIds, applicationId -> {
            ServiceResult<ProcessRole> leadApplicantResult = getProcessRoles(applicationId, LEADAPPLICANT).andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail);
            return leadApplicantResult.andOnSuccessReturn(leadApplicant -> Pair.of(applicationId, new UserNotificationTarget(leadApplicant.getUser())));
        });
    }

    private void removeAssessorFeedbackFileFromApplication(Application application) {
        application.setAssessorFeedbackFileEntry(null);
    }

    private ServiceResult<FileEntry> getAssessorFeedbackFileEntry(Application application) {
        if (application.getAssessorFeedbackFileEntry() == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        } else {
            return serviceSuccess(application.getAssessorFeedbackFileEntry());
        }
    }

    private FileEntryResource linkAssessorFeedbackFileToApplication(Application application, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        linkAssessorFeedbackFileEntryToApplication(fileEntry, application);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private void linkAssessorFeedbackFileEntryToApplication(FileEntry fileEntry, Application application) {
        application.setAssessorFeedbackFileEntry(fileEntry);
    }

}
