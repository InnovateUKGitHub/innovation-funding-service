package org.innovateuk.ifs.project.documents.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
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
    ServiceResult<List<String>> getValidMediaTypesForDocument(long documentConfigId);

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'UPLOAD_OTHER_DOCUMENTS')")
    ServiceResult<FileEntryResource> createDocumentFileEntry(long projectId, long documentConfigId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

}
