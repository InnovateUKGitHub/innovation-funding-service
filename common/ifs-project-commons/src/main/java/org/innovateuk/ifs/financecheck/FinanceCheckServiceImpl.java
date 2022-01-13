package org.innovateuk.ifs.financecheck;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.upload.service.AttachmentRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling finance checks functionality
 */
@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    @Qualifier("projectFinance")
    private AttachmentRestService attachmentRestService;

    @Autowired
    private ProjectFinanceQueriesRestService queryService;

    @Autowired
    private ProjectFinanceNotesRestService noteService;

    @Override
    public FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return financeCheckRestService.getByProjectAndOrganisation(key.getProjectId(), key.getOrganisationId()).getSuccess();
    }

    @Override
    public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        return financeCheckRestService.getFinanceCheckSummary(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId) {
        return financeCheckRestService.getFinanceCheckOverview(projectId).toServiceResult();
    }

    @Override
    public FinanceCheckEligibilityResource getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        return financeCheckRestService.getFinanceCheckEligibilityDetails(projectId, organisationId).getSuccess();
    }

    @Override
    public ServiceResult<AttachmentResource> uploadFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        return attachmentRestService.upload(projectId, contentType, contentLength, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteFile(Long fileId) {
        return attachmentRestService.delete(fileId).toServiceResult();
    }

    @Override
    public ByteArrayResource downloadFile(Long fileId) {
        return attachmentRestService.download(fileId).getSuccess();
    }

    @Override
    public ServiceResult<AttachmentResource> getAttachment(Long attachmentId) {
        return attachmentRestService.find(attachmentId).toServiceResult();
    }

    @Override
    public FileEntryResource getAttachmentInfo(Long attachmentId) {
        AttachmentResource attachmentResource = attachmentRestService.find(attachmentId).getSuccess();
        return new FileEntryResource(attachmentResource.name, attachmentResource.mediaType, attachmentResource.sizeInBytes);
    }

    @Override
    public ServiceResult<Long> saveQuery(QueryResource query) {
        return queryService.create(query).toServiceResult();
    }

    @Override
    public ServiceResult<List<QueryResource>> getQueries(Long projectFinanceId) {
        return queryService.findAll(projectFinanceId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> saveQueryPost(PostResource post, long threadId) {
        return queryService.addPost(post, threadId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> closeQuery(Long queryId) {
        return queryService.close(queryId).toServiceResult();
    }

    @Override
    public ServiceResult<Long> saveNote(NoteResource note) {
        return noteService.create(note).toServiceResult();
    }

    @Override
    public ServiceResult<List<NoteResource>> loadNotes(Long projectFinanceId) {
        return noteService.findAll(projectFinanceId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> saveNotePost(PostResource post, long noteId) {
        return noteService.addPost(post, noteId).toServiceResult();
    }
}
