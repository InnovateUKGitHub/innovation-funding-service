package org.innovateuk.ifs.interview.transactional;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentResponseOutcomeRepository;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for responding to interview feedback.
 */
@Service
@Transactional
public class InterviewResponseServiceImpl implements InterviewResponseService {

    @Value("${ifs.data.service.file.storage.interview.response.max.filesize.bytes}")
    private Long maxFileSize;

    @Value("${ifs.data.service.file.storage.interview.response.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Autowired
    private InterviewAssignmentWorkflowHandler interviewAssignmentWorkflowHandler;

    @Autowired
    private InterviewAssignmentResponseOutcomeRepository interviewAssignmentResponseOutcomeRepository;

    @Override
    @Transactional
    public ServiceResult<Void> uploadResponse(String contentType, String contentLength, String originalFilename, long applicationId, HttpServletRequest request) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
                handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFileSize, request,
                        (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                                .andOnSuccessReturnVoid(created -> {
                                    InterviewAssignmentResponseOutcome outcome = new InterviewAssignmentResponseOutcome();
                                    outcome.setFileResponse(created.getValue());
                                    outcome.setProcess(interviewAssignment);
                                    interviewAssignmentWorkflowHandler.respondToInterviewPanel(interviewAssignment, outcome);
                                })).toServiceResult());
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteResponse(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccessReturnVoid(interviewAssignment -> {
            interviewAssignmentWorkflowHandler.withdrawResponse(interviewAssignment);
        });
    }

    @Override
    public ServiceResult<FileAndContents> downloadResponse(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
                fileEntryService.findOne(interviewAssignment.getResponse().getFileResponse().getId())
                        .andOnSuccess(this::getFileAndContents));
    }

    @Override
    public ServiceResult<FileEntryResource> findResponse(long applicationId) {
        return findAssignmentByApplicationId(applicationId).andOnSuccess(interviewAssignment ->
                ofNullable(interviewAssignment.getResponse())
                    .map(InterviewAssignmentResponseOutcome::getFileResponse)
                    .map(FileEntry::getId)
                    .map(fileId -> fileEntryService.findOne(fileId))
                    .orElse(serviceSuccess(null)));
    }

    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }

    private ServiceResult<InterviewAssignment> findAssignmentByApplicationId(long applicationId) {
        return find(interviewAssignmentRepository.findOneByTargetId(applicationId), notFoundError(InterviewAssignment.class, applicationId));
    }
}