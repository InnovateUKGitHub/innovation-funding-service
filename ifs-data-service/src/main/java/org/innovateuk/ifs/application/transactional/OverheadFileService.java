package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.method.P;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for handling the overhead calculation file upload with permission definitions
 */
public interface OverheadFileService {
    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @NotSecured("should be secured to check if user has access application finance of which overhead is part or not")
    ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @NotSecured("should be secured to check if user has access application finance of which overhead is part or not")
    ServiceResult<FileAndContents> getFileEntryContents(long overheadId);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @NotSecured("should be secured to check if user has access application finance of which overhead is part or not")
    ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @NotSecured("should be secured to check if user has access application finance of which overhead is part or not")
    ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    //TODO: INFUND-6788: Implement more specific permissions on finance level
    @NotSecured("should be secured to check if user has access application finance of which overhead is part or not")
    ServiceResult<Void> deleteFileEntry(long overheadId);
}