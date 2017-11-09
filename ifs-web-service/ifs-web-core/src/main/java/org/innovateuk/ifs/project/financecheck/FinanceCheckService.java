package org.innovateuk.ifs.project.financecheck;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Optional;

public interface FinanceCheckService {

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    FinanceCheckEligibilityResource getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<AttachmentResource> uploadFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> deleteFile(Long fileId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ByteArrayResource downloadFile(Long fileId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<AttachmentResource> getAttachment(Long attachmentId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    FileEntryResource getAttachmentInfo(Long attachmentId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Long> saveQuery(QueryResource query);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveQueryPost(PostResource post, long threadId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<List<QueryResource>> getQueries(Long projectFinanceId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Long> saveNote(NoteResource note);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<List<NoteResource>> loadNotes(Long projectFinanceId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveNotePost(PostResource post, long noteId);
}
