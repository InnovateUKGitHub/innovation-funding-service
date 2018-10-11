package org.innovateuk.ifs.project.documents.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Project Documents processing work
 */
public interface DocumentsService {

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    @SecuredBySpring(value = "XXX", description = "XXX")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<List<String>> getValidMediaTypesForDocument(long documentConfigId);

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    @SecuredBySpring(value = "XXX", description = "XXX")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<FileEntryResource> createDocumentFileEntry(long projectId, long documentConfigId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'DOWNLOAD_OTHER_DOCUMENTS')")
    @SecuredBySpring(value = "XXX", description = "XXX")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<FileAndContents> getFileContents(long projectId, long documentConfigId);

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_OTHER_DOCUMENTS_DETAILS')")
    @SecuredBySpring(value = "XXX", description = "XXX")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<FileEntryResource> getFileEntryDetails(long projectId, long documentConfigId);

}
