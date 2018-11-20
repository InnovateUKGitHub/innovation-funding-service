package org.innovateuk.ifs.project.documents.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

public interface DocumentsService {

    @SecuredBySpring(value = "GET_VALID_MEDIA_TYPES", description = "Any authenticated user can get valid media types for a document")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<List<String>> getValidMediaTypesForDocument(long documentConfigId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_DOCUMENT')")
    ServiceResult<FileEntryResource> createDocumentFileEntry(long projectId, long documentConfigId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_DOCUMENT')")
    ServiceResult<FileAndContents> getFileContents(long projectId, long documentConfigId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_DOCUMENT')")
    ServiceResult<FileEntryResource> getFileEntryDetails(long projectId, long documentConfigId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DELETE_DOCUMENT')")
    ServiceResult<Void> deleteDocument(long projectId, long documentConfigId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'SUBMIT_DOCUMENT')")
    ServiceResult<Void> submitDocument(long projectId, long documentConfigId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'APPROVE_DOCUMENT')")
    ServiceResult<Void> documentDecision(long projectId, long documentConfigId, ProjectDocumentDecision decision);

}
