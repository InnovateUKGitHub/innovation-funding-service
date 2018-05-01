package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentMessageOutcomeRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for uploading feedback for {@link InterviewAssignment}.
 */
@Service
@Transactional
public class InterviewApplicationFeedbackServiceImpl implements InterviewApplicationFeedbackService {

    @Value("${ifs.data.service.file.storage.interview.feedback.max.filesize.bytes}")
    private Long maxFileSize;

    @Value("${ifs.data.service.file.storage.interview.feedback.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private InterviewAssignmentMessageOutcomeRepository interviewAssignmentMessageOutcomeRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Override
    @Transactional
    public ServiceResult<Void> uploadFeedback(String contentType, String contentLength, String originalFilename, long applicationId, HttpServletRequest request) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
            handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFileSize, request,
                (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                        .andOnSuccessReturnVoid(created -> {
                            InterviewAssignmentMessageOutcome outcome = new InterviewAssignmentMessageOutcome();
                            outcome.setAssessmentInterviewPanel(interviewAssignment);
                            outcome.setFeedback(created.getValue());
                            interviewAssignment.getProcessOutcomes().add(outcome);
                        })).toServiceResult());
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteFeedback(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment -> {
            long fileId = interviewAssignment.getMessage().getFeedback().getId();
            return fileService.deleteFileIgnoreNotFound(fileId).andOnSuccessReturnVoid(() -> {
                InterviewAssignmentMessageOutcome message = interviewAssignment.getMessage();
                interviewAssignment.getProcessOutcomes().remove(message);
                interviewAssignmentMessageOutcomeRepository.delete(message);
            });
        });
    }

    @Override
    public ServiceResult<FileAndContents> downloadFeedback(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
            fileEntryService.findOne(interviewAssignment.getMessage().getFeedback().getId())
                    .andOnSuccess(this::getFileAndContents));
    }

    @Override
    public ServiceResult<FileEntryResource> findFeedback(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
                fileEntryService.findOne(interviewAssignment.getMessage().getFeedback().getId()));
    }

    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }

    private ServiceResult<InterviewAssignment> findAssignmentByApplicationId(long applicationId) {
        return find(interviewAssignmentRepository.findOneByTargetId(applicationId), notFoundError(InterviewAssignment.class, applicationId));
    }
}