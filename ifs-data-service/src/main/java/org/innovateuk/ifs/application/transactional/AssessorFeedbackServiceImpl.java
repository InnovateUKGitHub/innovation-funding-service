package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;

@Service
public class AssessorFeedbackServiceImpl extends BaseTransactionalService implements AssessorFeedbackService {

    private static final Predicate<Application> applicationApprovedFilter = application -> APPROVED == application.getApplicationProcess().getActivityState();

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
