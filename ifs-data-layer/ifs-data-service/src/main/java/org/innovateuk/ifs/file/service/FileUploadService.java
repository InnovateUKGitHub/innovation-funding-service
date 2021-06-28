package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.io.InputStream;
import java.util.function.Supplier;

public interface FileUploadService {

    @SecuredBySpring(value = "UPLOAD_FILE", description = "A system maintainer can upload file.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<FileEntryResource> uploadFile(@P("uploadedFileType")String uploadedFileType, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);
}
