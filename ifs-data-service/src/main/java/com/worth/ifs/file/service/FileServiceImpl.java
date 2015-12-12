package com.worth.ifs.file.service;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.*;
import static com.worth.ifs.transactional.ServiceFailure.error;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsolutePath;
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsolutePathString;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * store and retrieve files.
 */
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_FOLDERS, //
        UNABLE_TO_CREATE_FILE, //
        DUPLICATE_FILE_CREATED, //
        UNABLE_TO_FIND_FILE, //
    }

    @Autowired
    private FileStorageStrategy fileStorageStrategy;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    public Either<ServiceFailure, ServiceSuccess<File>> createFile(FileEntryResource resource) {
        return handlingErrors(() ->
                saveFileEntry(resource).
                map(savedFileEntry -> doCreateFile(savedFileEntry)).
                map(file -> successResponse(file)),
                UNABLE_TO_CREATE_FILE);
    }
    @Override
    public Either<ServiceFailure, ServiceSuccess<File>> getFileByFileEntryId(Long fileEntryId) {
        return handlingErrors(() ->
                findFileEntry(fileEntryId).
                map(fileEntry -> findFile(fileEntry)).
                map(file -> successResponse(file)),
                UNABLE_TO_FIND_FILE);
    }

    private Either<ServiceFailure, File> doCreateFile(FileEntry savedFileEntry) {
        Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(savedFileEntry);
        List<String> pathElements = filePathAndName.getLeft();
        String filename = filePathAndName.getRight();
        return doCreateFile(pathElements, filename);
    }

    private Either<ServiceFailure, FileEntry> saveFileEntry(FileEntryResource resource) {
        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
        return right(fileEntryRepository.save(fileEntry));
    }

    private Either<ServiceFailure, FileEntry> findFileEntry(Long fileEntryId) {
        return getOrFail(() -> fileEntryRepository.findOne(fileEntryId), error(UNABLE_TO_FIND_FILE));
    }

    private Either<ServiceFailure, File> findFile(FileEntry fileEntry) {
        return getOrFail(() -> {

            Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(fileEntry);
            List<String> pathElements = filePathAndName.getLeft();
            String filename = filePathAndName.getRight();
            File expectedFile = new File(pathElementsToAbsolutePathString(pathElements), filename);
            return expectedFile.exists() ? expectedFile : null;

        }, error(UNABLE_TO_FIND_FILE));
    }

    private Either<ServiceFailure, File> doCreateFile(List<String> pathElements, String filename) {

        Path foldersPath = pathElementsToAbsolutePath(pathElements);

        return createFolders(foldersPath).map(createdFolders -> {

            File fileToCreate = new File(createdFolders.toString(), filename);
            return !fileToCreate.exists() ? createFileOrFail(fileToCreate) : errorResponse(DUPLICATE_FILE_CREATED);
        });
    }

    private Either<ServiceFailure, Path> createFolders(Path path) {
        try {
            return right(Files.createDirectories(path));
        } catch (IOException e) {
            return errorResponse(UNABLE_TO_CREATE_FOLDERS, e);
        }
    }

    private Either<ServiceFailure, File> createFileOrFail(File createdFile) {
        try {
            return createdFile.createNewFile() ? right(createdFile) : errorResponse(UNABLE_TO_CREATE_FILE);
        } catch (IOException e) {
            return errorResponse(UNABLE_TO_CREATE_FILE, e);
        }
    }
}
