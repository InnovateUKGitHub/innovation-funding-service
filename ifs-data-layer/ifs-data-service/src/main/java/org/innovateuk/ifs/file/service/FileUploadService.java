package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

public interface FileUploadService {

    @SecuredBySpring(value = "CREATE_FILE_ENTRY", description = "An ifs admin can create file.")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<FileEntryResource> createFileEntry(@P("uploadedFileType")String uploadedFileType, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @SecuredBySpring(value = "DELETE_FILE_ENTRY", description = "An ifs admin can delete file.")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<Void> deleteFileEntry(@P("uploadId")long uploadId);

    @SecuredBySpring(value = "GET_FILE_ENTRY", description = "An ifs admin can delete file contents.")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<FileAndContents> getFileContents(@P("uploadId")long uploadId);

    @SecuredBySpring(value = "GET_FILE_ENTRY", description = "An ifs admin can delete file contents.")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<List<FileEntryResource>> getAllUploadedFileEntryResources();
}
