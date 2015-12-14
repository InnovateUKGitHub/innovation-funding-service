package com.worth.ifs.file.service;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * This interface represents a Service that is able to store and retrieve files, based upon information
 * in FileEntryResource and its subclasses.
 */
public interface FileService {

    Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> createFile(FileEntryResource file);

    Either<ServiceFailure, ServiceSuccess<File>> getFileByFileEntryId(Long fileEntryId);
}
