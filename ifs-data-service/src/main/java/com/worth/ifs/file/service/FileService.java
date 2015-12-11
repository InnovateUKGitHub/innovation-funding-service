package com.worth.ifs.file.service;

import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;

import java.io.File;

/**
 * This interface represents a Service that is able to store and retrieve files, based upon information
 * in FileEntryResource and its subclasses.
 */
public interface FileService {

    Either<ServiceFailure, ServiceSuccess<File>> createFile(FileEntryResource file);

    Either<ServiceFailure, ServiceSuccess<File>> getFileByFileEntryId(Long fileEntryId);
}
