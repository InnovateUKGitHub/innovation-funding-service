package org.innovateuk.ifs.file.transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * validate, store and retrieve files.
 */
@Service
public class FileServiceImpl extends RootTransactionalService implements FileService {

    private final Tika tika = new Tika();
    private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

    @Autowired
    @Qualifier("temporaryHoldingFileStorageStrategy")
    private FileStorageStrategy temporaryHoldingFileStorageStrategy;

    @Autowired
    @Qualifier("quarantinedFileStorageStrategy")
    private FileStorageStrategy quarantinedFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;

    @Autowired
    @Qualifier("finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    @Transactional
    public ServiceResult<Pair<File, FileEntry>> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFileForValidation(resource.getName(), inputStreamSupplier).andOnSuccess(validationFile -> {
            try {
                return find(
                        validateMediaType(validationFile, MediaType.parseMediaType(resource.getMediaType())),
                        validateContentLength(resource.getFilesizeBytes(), validationFile)).
                        andOnSuccess((mediaType, contentLength) ->
                            saveFileEntry(resource).andOnSuccess(savedFileEntry ->
                                    createFileForFileEntry(savedFileEntry, validationFile))
                        );
            } finally {
                deleteFile(validationFile);
            }
        });
    }

    @Override
    public ServiceResult<Supplier<InputStream>> getFileByFileEntryId(Long fileEntryId) {
        return findFileEntry(fileEntryId).
                andOnSuccess(this::findFileForGet).
                andOnSuccess(fileAndStorageLocation -> getInputStreamSupplier(fileAndStorageLocation.getKey()));
    }

    @Override
    @Transactional
    public ServiceResult<Pair<File, FileEntry>> updateFile(FileEntryResource fileToUpdate, Supplier<InputStream> inputStreamSupplier) {
        return findFileEntry(fileToUpdate.getId()).
                andOnSuccess(updatedFile -> doFileValidationAndUpdate(updatedFile, inputStreamSupplier));
    }

    private FailingOrSucceedingResult<Pair<File, FileEntry>, ServiceFailure> doFileValidationAndUpdate(FileEntry updatedFile, Supplier<InputStream> inputStreamSupplier) {
        return createTemporaryFileForValidation(updatedFile.getName(), inputStreamSupplier).andOnSuccess(validationFile -> {
            try {
                return find(
                        validateMediaType(validationFile, MediaType.parseMediaType(updatedFile.getMediaType())),
                        validateContentLength(updatedFile.getFilesizeBytes(), validationFile)).
                        andOnSuccess((mediaType, contentLength) ->

                                updateFileEntry(updatedFile).andOnSuccess(updatedFileEntry -> updateFileForFileEntry(updatedFileEntry, validationFile))
                        );
            } finally {
                deleteFile(validationFile);
            }
        });
    }

    @Override
    @Transactional
    public ServiceResult<FileEntry> deleteFile(long fileEntryId) {

        return findFileEntry(fileEntryId).
            andOnSuccess(fileEntry -> findFileForDelete(fileEntry).
            andOnSuccess(fileAndStorageLocation -> {
                fileEntryRepository.delete(fileEntry);
                FileStorageStrategy storageLocation = fileAndStorageLocation.getValue();
                return storageLocation.deleteFile(fileEntry).andOnSuccessReturn(() -> fileEntry);
            }));
    }

    private ServiceResult<FileEntry> updateFileEntry(FileEntry updatedFileDetails) {
        FileEntry updated = fileEntryRepository.save(updatedFileDetails);
        return serviceSuccess(updated);
    }

    private ServiceResult<Pair<File, FileEntry>> updateFileForFileEntry(FileEntry existingFileEntry, File tempFile) {

        boolean fileAlreadyInHoldingStorageLocation = temporaryHoldingFileStorageStrategy.exists(existingFileEntry);

        if (fileAlreadyInHoldingStorageLocation) {
            return temporaryHoldingFileStorageStrategy.deleteFile(existingFileEntry).
                    andOnSuccess(() -> createFileForFileEntry(existingFileEntry, tempFile));
        } else {
            return findFileInAnyLocation(existingFileEntry).
                    andOnSuccess(existingFileAndStorage -> createFileForFileEntry(existingFileEntry, tempFile).
                    andOnSuccess(updatedFile -> existingFileAndStorage.getRight().deleteFile(existingFileEntry).
                    andOnSuccessReturn(() -> updatedFile)));
        }
    }

    private ServiceResult<File> createTemporaryFileForValidation(String originalFilename, Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFile("filevalidation", originalFilename, new Error(FILES_UNABLE_TO_CREATE_FILE)).
                andOnSuccess(tempFile -> updateFileWithContents(tempFile, inputStreamSupplier)).
                andOnSuccess(this::pathToFile);
    }

    private ServiceResult<File> validateContentLength(long filesizeBytes, File tempFile) {

        if (tempFile.length() == filesizeBytes) {
            return serviceSuccess(tempFile);
        } else {
            LOG.error("Reported filesize was " + filesizeBytes + " bytes but actual file is " + tempFile.length() + " bytes");
            return serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE, tempFile.length()));
        }
    }

    private ServiceResult<File> validateMediaType(File file, MediaType mediaType) {
        final String detectedContentType;
        try {
            detectedContentType = tika.detect(file.toPath());
        } catch (IOException e) {
            LOG.error("Unable to probe file for Content Type", e);
            return serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE));
        }

        if (detectedContentType == null) {
            LOG.warn("Content Type of file " + file + " could not be determined - returning as valid because not explicitly detectable");
            return serviceSuccess(file);
        } else if (mediaType.toString().equals(detectedContentType)) {
            return serviceSuccess(file);
        } else {
            LOG.warn("Content Type of file has been detected as " + detectedContentType + " but was reported as being " + mediaType);
            return serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE, detectedContentType));
        }
    }

    private ServiceResult<Path> createTemporaryFile(String prefix, String filename, Error errorMessage) {
        try {
            String extension = FilenameUtils.getExtension(filename);
            return serviceSuccess(Files.createTempFile(prefix, !isBlank(extension) ? "." + extension : ""));
        } catch (IOException e) {
            LOG.error("Error creating temporary file for " + prefix, e);
            return serviceFailure(errorMessage);
        }
    }

    private ServiceResult<Supplier<InputStream>> getInputStreamSupplier(File file) {
        return serviceSuccess(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                LOG.error("Unable to supply FileInputStream for file " + file, e);
                throw new IllegalStateException("Unable to supply FileInputStream for file " + file, e);
            }
        });
    }

    private ServiceResult<Pair<File, FileEntry>> createFileForFileEntry(FileEntry savedFileEntry, File tempFile) {
        return temporaryHoldingFileStorageStrategy.createFile(savedFileEntry, tempFile).
                andOnSuccessReturn(file -> Pair.of(file, savedFileEntry));
    }

    private ServiceResult<FileEntry> saveFileEntry(FileEntryResource resource) {
        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
        return serviceSuccess(fileEntryRepository.save(fileEntry));
    }

    private ServiceResult<FileEntry> findFileEntry(Long fileEntryId) {
        return find(fileEntryRepository.findOne(fileEntryId), notFoundError(FileEntry.class, fileEntryId));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileForGet(FileEntry fileEntry) {
        return validateFileNotUnsafe(fileEntry).andOnSuccess(() -> findFileInSafeLocation(fileEntry));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileForDelete(FileEntry fileEntry) {
        return findFileInAnyLocation(fileEntry);
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInSafeLocation(FileEntry fileEntry) {
        return findFileInFinalFileStorageLocation(fileEntry).andOnFailure(() -> findFileInScannedStorageLocation(fileEntry));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInAnyLocation(FileEntry fileEntry) {
        return findFileInFinalFileStorageLocation(fileEntry).
                andOnFailure(() -> findFileInScannedStorageLocation(fileEntry)).
                andOnFailure(() -> findFileInHoldingStorageLocation(fileEntry)).
                andOnFailure(() -> findFileInQuarantinedStorageLocation(fileEntry));
    }

    private ServiceResult<Void> validateFileNotUnsafe(FileEntry fileEntry) {
        return validateNotInQuarantine(fileEntry).andOnSuccess(() -> validateNotAwaitingScanning(fileEntry));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInFinalFileStorageLocation(FileEntry fileEntry) {
        return findFileInStorageLocation(fileEntry, finalFileStorageStrategy).andOnSuccessReturn(file -> Pair.of(file, finalFileStorageStrategy));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInScannedStorageLocation(FileEntry fileEntry) {
        return findFileInStorageLocation(fileEntry, scannedFileStorageStrategy).andOnSuccessReturn(file -> Pair.of(file, scannedFileStorageStrategy));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInHoldingStorageLocation(FileEntry fileEntry) {
        return findFileInStorageLocation(fileEntry, temporaryHoldingFileStorageStrategy).andOnSuccessReturn(file -> Pair.of(file, temporaryHoldingFileStorageStrategy));
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInQuarantinedStorageLocation(FileEntry fileEntry) {
        return findFileInStorageLocation(fileEntry, quarantinedFileStorageStrategy).andOnSuccessReturn(file -> Pair.of(file, quarantinedFileStorageStrategy));
    }

    private ServiceResult<File> findFileInStorageLocation(FileEntry fileEntry, FileStorageStrategy fileStorageStrategy) {
        return fileStorageStrategy.getFile(fileEntry);
    }

    private ServiceResult<Void> validateNotAwaitingScanning(FileEntry fileEntry) {
        if (temporaryHoldingFileStorageStrategy.exists(fileEntry)) {
            return serviceFailure(forbiddenError(FILES_FILE_AWAITING_VIRUS_SCAN));
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<Void> validateNotInQuarantine(FileEntry fileEntry) {
        if (quarantinedFileStorageStrategy.exists(fileEntry)) {
            return serviceFailure(forbiddenError(FILES_FILE_QUARANTINED));
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<File> pathToFile(Path path) {
        return serviceSuccess(path.toFile());
    }

    private ServiceResult<Path> updateFileWithContents(Path file, Supplier<InputStream> inputStreamSupplier) {

        try {
            try (InputStream sourceInputStream = inputStreamSupplier.get()) {
                try {
                    Files.copy(sourceInputStream, file, StandardCopyOption.REPLACE_EXISTING);
                    return serviceSuccess(file);
                } catch (IOException e) {
                    LOG.error("Could not write data to file " + file, e);
                    return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
                }
            }
        } catch (IOException e) {
            LOG.error("Error closing file stream for file " + file, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }
    }

    private void deleteFile(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            LOG.error("Error deleting file", e);
        }
    }
}
