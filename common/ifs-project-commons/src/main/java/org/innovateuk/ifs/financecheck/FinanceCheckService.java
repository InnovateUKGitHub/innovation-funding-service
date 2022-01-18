package org.innovateuk.ifs.financecheck;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface FinanceCheckService {

    FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    FinanceCheckEligibilityResource getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    ServiceResult<AttachmentResource> uploadFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes);

    ServiceResult<Void> deleteFile(Long fileId);

    ByteArrayResource downloadFile(Long fileId);

    ServiceResult<AttachmentResource> getAttachment(Long attachmentId);

    FileEntryResource getAttachmentInfo(Long attachmentId);

    ServiceResult<Long> saveQuery(QueryResource query);

    ServiceResult<Void> saveQueryPost(PostResource post, long threadId);

    ServiceResult<Void> closeQuery(Long queryId);

    ServiceResult<List<QueryResource>> getQueries(Long projectFinanceId);

    ServiceResult<Long> saveNote(NoteResource note);

    ServiceResult<List<NoteResource>> loadNotes(Long projectFinanceId);

    ServiceResult<Void> saveNotePost(PostResource post, long noteId);
}
