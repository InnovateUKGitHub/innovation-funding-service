package com.worth.ifs.file.transactional;

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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.file.transactional.FileServiceImpl.ServiceFailures.*;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsolutePath;
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsolutePathString;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * validate, store and retrieve files.
 */
@Service
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

    enum ServiceFailures {
        UNABLE_TO_CREATE_FOLDERS, //
        UNABLE_TO_CREATE_FILE, //
        DUPLICATE_FILE_CREATED, //
        UNABLE_TO_FIND_FILE, //
        INCORRECTLY_REPORTED_MEDIA_TYPE, //
        INCORRECTLY_REPORTED_FILESIZE, //
    }

    @Autowired
    private FileStorageStrategy fileStorageStrategy;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    public Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {
        return handlingErrors(UNABLE_TO_CREATE_FILE, () ->
            createTemporaryFileForValidation(inputStreamSupplier).map(validationFile -> {
                try {
                    return validateMediaType(validationFile, resource.getMediaType()).
                            map(tempFile -> validateContentLength(resource.getFilesizeBytes(), tempFile)).
                            map(tempFile -> saveFileEntry(resource).
                                    map(savedFileEntry -> doCreateFile(savedFileEntry, tempFile)).
                                    map(fileAndFileEntry -> successResponse(fileAndFileEntry))
                            );
                } finally {
                    deleteFile(validationFile);
                }
            }));
    }

    private void deleteFile(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            LOG.error("Unable to delete " + file);
        }
    }

    @Override
    public Either<ServiceFailure, ServiceSuccess<Supplier<InputStream>>> getFileByFileEntryId(Long fileEntryId) {
        return handlingErrors(UNABLE_TO_FIND_FILE, () ->
                findFileEntry(fileEntryId).
                map(fileEntry -> findFile(fileEntry)).
                map(fileEntry -> getInputStreamSuppier(fileEntry)).
                map(inputStream -> successResponse(inputStream))
        );
    }

    private Either<ServiceFailure, File> createTemporaryFileForValidation(Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFile("filevalidation", UNABLE_TO_CREATE_FILE).
                map(tempFile -> updateFileWithContents(tempFile, inputStreamSupplier)).
                map(tempFile -> pathToFile(tempFile));
    }

    private Either<ServiceFailure, File> validateContentLength(long filesizeBytes, File tempFile) {

        if (tempFile.length() == filesizeBytes) {
            return right(tempFile);
        } else {
            LOG.error("Reported filesize was " + filesizeBytes + " bytes but actual file is " + tempFile.length() + " bytes");
            return errorResponse(INCORRECTLY_REPORTED_FILESIZE);
        }
    }

    private Either<ServiceFailure, File> validateMediaType(File file, MediaType mediaType) {
        final String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            LOG.error("Unable to probe file for Content Type", e);
            return errorResponse(INCORRECTLY_REPORTED_MEDIA_TYPE);
        }

        if (mediaType.toString().equals(contentType)) {
            return right(file);
        } else {
            LOG.warn("Content Type of file has been detected as " + contentType + " but was reported as being " + mediaType);
            return errorResponse(INCORRECTLY_REPORTED_MEDIA_TYPE);
        }
    }

    private Either<ServiceFailure, Path> createTemporaryFile(String prefix, Enum<?> errorMessage) {
        try {
            return right(Files.createTempFile(prefix, ""));
        } catch (IOException e) {
            LOG.error("Error creating temporary file for " + prefix, e);
            return errorResponse(errorMessage);
        }
    }

    private Either<ServiceFailure, Supplier<InputStream>> getInputStreamSuppier(File file) {
        return right(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                LOG.error("Unable to supply FileInputStream for file " + file, e);
                throw new IllegalStateException("Unable to supply FileInputStream for file " + file, e);
            }
        });
    }

    private Either<ServiceFailure, Pair<File, FileEntry>> doCreateFile(FileEntry savedFileEntry, File tempFile) {
        Pair<List<String>, String> filePathAndName = fileStorageStrategy.getAbsoluteFilePathAndName(savedFileEntry);
        List<String> pathElements = filePathAndName.getLeft();
        String filename = filePathAndName.getRight();
        return doCreateFile(pathElements, filename, tempFile).map(file -> right(Pair.of(file, savedFileEntry)));
    }

    private Either<ServiceFailure, FileEntry> saveFileEntry(FileEntryResource resource) {
        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
        return right(fileEntryRepository.save(fileEntry));
    }

    private Either<ServiceFailure, FileEntry> findFileEntry(Long fileEntryId) {
        return getOrFail(() -> fileEntryRepository.findOne(fileEntryId), () -> {
            LOG.error("Could not find FileEntry for id " + fileEntryId);
            return ServiceFailure.error(UNABLE_TO_FIND_FILE);
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
            return ServiceFailure.error(UNABLE_TO_FIND_FILE);
        });
    }

    private Either<ServiceFailure, File> doCreateFile(List<String> pathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToAbsolutePath(pathElements);

        return createFolders(foldersPath).
                map(createdFolders -> copyTempFileToTargetFile(createdFolders, filename, tempFile));
    }

    private Either<ServiceFailure, File> copyTempFileToTargetFile(Path targetFolder, String targetFilename, File tempFile) {
        try {
            File fileToCreate = new File(targetFolder.toString(), targetFilename);

            if (fileToCreate.exists()) {
                LOG.error("File " + targetFilename + " already existed in target path " + targetFolder + ".  Cannot create a new one here.");
                return errorResponse(DUPLICATE_FILE_CREATED);
            }

            Path targetFile = Files.copy(tempFile.toPath(), Paths.get(targetFolder.toString(), targetFilename));
            return right(targetFile.toFile());
        } catch (IOException e) {
            LOG.error("Unable to copy temporary file " + tempFile + " to target folder " + targetFolder + " and file " + targetFilename);
            return errorResponse(UNABLE_TO_CREATE_FILE);
        }
    }

    private Either<ServiceFailure, Path> createFolders(Path path) {
        try {
            return right(Files.createDirectories(path));
        } catch (IOException e) {
            LOG.error("Error creating folders " + path, e);
            return errorResponse(UNABLE_TO_CREATE_FOLDERS, e);
        }
    }

    private Either<ServiceFailure, File> pathToFile(Path path) {
        return right(path.toFile());
    }

    private Either<ServiceFailure, Path> updateFileWithContents(Path file, Supplier<InputStream> inputStreamSupplier) {

        try {
            try (InputStream sourceInputStream = inputStreamSupplier.get()) {
                try {
                    Files.copy(sourceInputStream, file, StandardCopyOption.REPLACE_EXISTING);
                    return right(file);
                } catch (IOException e) {
                    LOG.error("Could not write data to file " + file, e);
                    return errorResponse(UNABLE_TO_CREATE_FILE, e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error closing file stream for file " + file, e);
            return errorResponse(UNABLE_TO_CREATE_FILE, e);
        }
    }
}
