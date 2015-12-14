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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;

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
@Service
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

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
    public Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {
        return handlingErrors(() ->
                saveFileEntry(resource).
                map(savedFileEntry -> doCreateFile(savedFileEntry, inputStreamSupplier)).
                map(fileAndFileEntry -> successResponse(fileAndFileEntry)),
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

    private Either<ServiceFailure, Pair<File, FileEntry>> doCreateFile(FileEntry savedFileEntry, Supplier<InputStream> inputStreamSupplier) {
        Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(savedFileEntry);
        List<String> pathElements = filePathAndName.getLeft();
        String filename = filePathAndName.getRight();
        return doCreateFile(pathElements, filename, inputStreamSupplier).map(file -> right(Pair.of(file, savedFileEntry)));
    }

    private Either<ServiceFailure, FileEntry> saveFileEntry(FileEntryResource resource) {
        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
        return right(fileEntryRepository.save(fileEntry));
    }

    private Either<ServiceFailure, FileEntry> findFileEntry(Long fileEntryId) {
        return getOrFail(() -> fileEntryRepository.findOne(fileEntryId), () -> {
            LOG.error("Could not find FileEntry for id " + fileEntryId);
            return error(UNABLE_TO_FIND_FILE);
        });
    }

    private Either<ServiceFailure, File> findFile(FileEntry fileEntry) {
        return getOrFail(() -> {

            Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(fileEntry);
            List<String> pathElements = filePathAndName.getLeft();
            String filename = filePathAndName.getRight();
            File expectedFile = new File(pathElementsToAbsolutePathString(pathElements), filename);
            return expectedFile.exists() ? expectedFile : null;

        }, () -> {
            LOG.error("Could not find File for FileEntry with id " + fileEntry.getId());
            return error(UNABLE_TO_FIND_FILE);
        });
    }

    private Either<ServiceFailure, File> doCreateFile(List<String> pathElements, String filename, Supplier<InputStream> inputStreamSupplier) {

        Path foldersPath = pathElementsToAbsolutePath(pathElements);

        return createFolders(foldersPath).map(createdFolders ->
               createNewFile(createdFolders.toString(), filename).map(newFile ->
               updateFileWithContents(newFile, inputStreamSupplier)
           ));
    }

    private Either<ServiceFailure, Path> createFolders(Path path) {
        try {
            return right(Files.createDirectories(path));
        } catch (IOException e) {
            LOG.error("Error creating folders " + path, e);
            return errorResponse(UNABLE_TO_CREATE_FOLDERS, e);
        }
    }

    private Either<ServiceFailure, File> createNewFile(String pathToFile, String filename) {

        File fileToCreate = new File(pathToFile, filename);

        if (fileToCreate.exists()) {
            LOG.error("File " + filename + " already existed in target path " + pathToFile + ".  Cannot create a new one here.");
            return errorResponse(DUPLICATE_FILE_CREATED);
        }

        try {
            return fileToCreate.createNewFile() ? right(fileToCreate) : errorResponse(UNABLE_TO_CREATE_FILE);
        } catch (IOException e) {
            LOG.error("Could not create new file " + filename + " in target path " + pathToFile, e);
            return errorResponse(UNABLE_TO_CREATE_FILE, e);
        }
    }

    private Either<ServiceFailure, File> updateFileWithContents(File createdFile, Supplier<InputStream> inputStreamSupplier) {

        try {
            Files.copy(inputStreamSupplier.get(), createdFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return right(createdFile);
        } catch (IOException e) {
            LOG.error("Could not write data to file " + createdFile.getPath(), e);
            return errorResponse(UNABLE_TO_CREATE_FILE, e);
        }
    }
}
