package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * This interface represents a Service that is able to store and retrieve files, based upon information
 * in FileEntryResource and its subclasses.
 */
public interface FileService {

    @NotSecured("This Service is to be used within other secured services")
    ServiceResult<Pair<File, FileEntry>> createFile(FileEntryResource file, Supplier<InputStream> inputStreamSupplier);

    @NotSecured("This Service is to be used within other secured services")
    ServiceResult<Supplier<InputStream>> getFileByFileEntryId(Long fileEntryId);

    @NotSecured("This Service is to be used within other secured services")
    ServiceResult<Pair<File, FileEntry>> updateFile(FileEntryResource updatedFile, Supplier<InputStream> inputStreamSupplier);

    @NotSecured("This Service is to be used within other secured services")
    ServiceResult<FileEntry> deleteFile(long fileEntryId);
}
