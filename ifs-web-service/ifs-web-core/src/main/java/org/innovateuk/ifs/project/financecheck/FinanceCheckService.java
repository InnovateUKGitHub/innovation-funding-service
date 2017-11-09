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

    @NotSecured("Not currently secured")
    FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @NotSecured("Not currently secured")
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    @NotSecured("Not currently secured")
    FinanceCheckEligibilityResource getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<AttachmentResource> uploadFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes);

    @NotSecured("Not currently secured")
    ServiceResult<Void> deleteFile(Long fileId);

    @NotSecured("Not currently secured")
    ByteArrayResource downloadFile(Long fileId);

    @NotSecured("Not currently secured")
    ServiceResult<AttachmentResource> getAttachment(Long attachmentId);

    @NotSecured("Not currently secured")
    FileEntryResource getAttachmentInfo(Long attachmentId);

    @NotSecured("Not currently secured")
    ServiceResult<Long> saveQuery(QueryResource query);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveQueryPost(PostResource post, long threadId);

    @NotSecured("Not currently secured")
    ServiceResult<List<QueryResource>> getQueries(Long projectFinanceId);

    @NotSecured("Not currently secured")
    ServiceResult<Long> saveNote(NoteResource note);

    @NotSecured("Not currently secured")
    ServiceResult<List<NoteResource>> loadNotes(Long projectFinanceId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveNotePost(PostResource post, long noteId);
}
