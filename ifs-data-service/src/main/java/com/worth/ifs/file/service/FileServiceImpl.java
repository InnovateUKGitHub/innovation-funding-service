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
import java.util.function.Function;

import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.*;
import static com.worth.ifs.transactional.ServiceFailure.error;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * store and retrieve files.
 */
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_FOLDERS, //
        UNABLE_TO_CREATE_FILE, //
        DUPLICATE_FILE_CREATED, //
        FILE_NOT_FOUND, //
    }

    @Autowired
    private FileStorageStrategy fileStorageStrategy;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    public Either<ServiceFailure, ServiceSuccess<File>> createFile(FileEntryResource resource) {

        return handlingErrors(() -> {

            FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
            FileEntry savedFileEntry = fileEntryRepository.save(fileEntry);

            Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(savedFileEntry);
            List<String> pathElements = filePathAndName.getLeft();
            String filename = filePathAndName.getRight();

            Either<ServiceFailure, File> createdFile = doCreateFile(pathElements, filename);
            return createdFile.map(file -> successResponse(file));

        }, UNABLE_TO_CREATE_FILE);
    }

    @Override
    public Either<ServiceFailure, ServiceSuccess<File>> getFileByFileEntryId(Long fileEntryId) {

        ServiceFailure fileNotFoundError = error(FILE_NOT_FOUND);

        Either<ServiceFailure, FileEntry> findFileEntry = getOrFail(() -> fileEntryRepository.findOne(fileEntryId), fileNotFoundError);

        Function<FileEntry, Either<ServiceFailure, File>> findFile = fileEntry ->
                getOrFail(() -> {

            Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(fileEntry);
            List<String> pathElements = filePathAndName.getLeft();
            String filename = filePathAndName.getRight();
            return new File(getFoldersPath(pathElements).toString(), filename);

        }, fileNotFoundError);

        Function<File, Either<ServiceFailure, File>> checkFileExists = file ->
                file.exists() ? right(file) : errorResponse(FILE_NOT_FOUND);

        return findFileEntry.map(findFile).map(checkFileExists).map(file -> successResponse(file));

    }

    private Either<ServiceFailure, File> doCreateFile(List<String> pathElements, String filename) {

        Path foldersPath = getFoldersPath(pathElements);

        return createFolders(foldersPath).map(createdFolders -> {

            File fileToCreate = new File(createdFolders.toString(), filename);
            return !fileToCreate.exists() ? createFileOrFail(fileToCreate) : errorResponse(DUPLICATE_FILE_CREATED);
        });
    }

    private Path getFoldersPath(List<String> pathElements) {
        return new File(pathElements.stream().reduce("", (pathSoFar, nextPathSegment) -> pathSoFar + File.separator + nextPathSegment)).toPath();
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
