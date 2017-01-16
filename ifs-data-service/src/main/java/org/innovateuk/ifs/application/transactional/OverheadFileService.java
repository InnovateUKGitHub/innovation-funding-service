package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for handling the overhead calculation file upload with permission definitions
 */
public interface OverheadFileService {
    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<FileAndContents> getFileEntryContents(long overheadId);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> deleteFileEntry(long overheadId);
}