package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
import static com.worth.ifs.util.FileFunctions.pathElementsToPath;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * validate, store and retrieve files.
 */
@Service
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

    @Autowired
    @Qualifier("initialFileStorageStrategy")
    private FileStorageStrategy initialFileStorageStrategy;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    public ServiceResult<Pair<File, FileEntry>> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFileForValidation(inputStreamSupplier).andOnSuccess(validationFile -> {
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
                andOnSuccess(this::findFile).
                andOnSuccess(this::getInputStreamSupplier);
    }

    @Override
    public ServiceResult<Pair<File, FileEntry>> updateFile(FileEntryResource updatedFile, Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFileForValidation(inputStreamSupplier).andOnSuccess(validationFile -> {
            try {
                return find(
                        validateMediaType(validationFile, MediaType.parseMediaType(updatedFile.getMediaType())),
                        validateContentLength(updatedFile.getFilesizeBytes(), validationFile)).
                        andOnSuccess((mediaType, contentLength) ->

                    updateFileEntry(updatedFile).
                            andOnSuccess(updatedFileEntry -> updateFileForFileEntry(updatedFileEntry, validationFile))
                );
            } finally {
                deleteFile(validationFile);
            }
        });
    }

    @Override
    public ServiceResult<FileEntry> deleteFile(long fileEntryId) {

        return findFileEntry(fileEntryId).
            andOnSuccess(fileEntry -> findFile(fileEntry).
            andOnSuccess(file -> {

                fileEntryRepository.delete(fileEntry);

                boolean fileDeletedSuccessfully = file.delete();

                if (fileDeletedSuccessfully) {
                    return serviceSuccess(fileEntry);
                } else {
                    return serviceFailure(new Error(FILES_UNABLE_TO_DELETE_FILE, FileEntry.class, fileEntryId));
                }
            }));
    }

    private ServiceResult<FileEntry> updateFileEntry(FileEntryResource updatedFileDetails) {
        FileEntry updated = fileEntryRepository.save(FileEntryResourceAssembler.valueOf(updatedFileDetails));
        return serviceSuccess(updated);
    }

    private ServiceResult<Pair<File, FileEntry>> updateFileForFileEntry(FileEntry existingFileEntry, File tempFile) {

        // TODO DW - INFUND-2220 - this code needs to be adapted to handle the need to update a file that could be still awaiting scanning, has been scanned etc
        Pair<List<String>, String> absoluteFilePathAndName = initialFileStorageStrategy.getAbsoluteFilePathAndName(existingFileEntry);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();

        return updateFileForFileEntry(pathElements, filename, tempFile).andOnSuccessReturn(file -> Pair.of(file, existingFileEntry));
    }

    private ServiceResult<File> createTemporaryFileForValidation(Supplier<InputStream> inputStreamSupplier) {

        return createTemporaryFile("filevalidation", new Error(FILES_UNABLE_TO_CREATE_FILE)).
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
            detectedContentType = Files.probeContentType(file.toPath());
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

    private ServiceResult<Path> createTemporaryFile(String prefix, Error errorMessage) {
        try {
            return serviceSuccess(Files.createTempFile(prefix, ""));
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

        Pair<List<String>, String> absoluteFilePathAndName = initialFileStorageStrategy.getAbsoluteFilePathAndName(savedFileEntry);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();
        return createFileForFileEntry(pathElements, filename, tempFile).andOnSuccessReturn(file -> Pair.of(file, savedFileEntry));
    }

    private ServiceResult<FileEntry> saveFileEntry(FileEntryResource resource) {
        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(resource);
        return serviceSuccess(fileEntryRepository.save(fileEntry));
    }

    private ServiceResult<FileEntry> findFileEntry(Long fileEntryId) {
        return find(fileEntryRepository.findOne(fileEntryId), notFoundError(FileEntry.class, fileEntryId));
    }

    private ServiceResult<File> findFile(FileEntry fileEntry) {

        // TODO DW - INFUND-2220 - this code needs to be amended to look in virus scanning / scanned folders
        Pair<List<String>, String> filePathAndName = initialFileStorageStrategy.getAbsoluteFilePathAndName(fileEntry);
        List<String> pathElements = filePathAndName.getLeft();
        String filename = filePathAndName.getRight();
        File expectedFile = new File(pathElementsToFile(pathElements), filename);

        if (expectedFile.exists()) {
            return serviceSuccess(expectedFile);
        } else {
            return serviceFailure(notFoundError(FileEntry.class, fileEntry.getId()));
        }
    }

    private ServiceResult<File> createFileForFileEntry(List<String> absolutePathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToPath(absolutePathElements);

        return createFolders(foldersPath).
                andOnSuccess(createdFolders -> copyTempFileToTargetFile(createdFolders, filename, tempFile));
    }

    private ServiceResult<File> updateFileForFileEntry(List<String> absolutePathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToPath(absolutePathElements);
        return updateExistingFileWithTempFile(foldersPath, filename, tempFile);
    }

    private ServiceResult<File> copyTempFileToTargetFile(Path targetFolder, String targetFilename, File tempFile) {
        try {
            File fileToCreate = new File(targetFolder.toString(), targetFilename);

            if (fileToCreate.exists()) {
                LOG.error("File " + targetFilename + " already existed in target path " + targetFolder + ".  Cannot create a new one here.");
                return serviceFailure(new Error(FILES_DUPLICATE_FILE_CREATED));
            }

            Path targetFile = Files.copy(tempFile.toPath(), Paths.get(targetFolder.toString(), targetFilename));
            return serviceSuccess(targetFile.toFile());
        } catch (IOException e) {
            LOG.error("Unable to copy temporary file " + tempFile + " to target folder " + targetFolder + " and file " + targetFilename, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }
    }

    private ServiceResult<File> updateExistingFileWithTempFile(Path targetFolder, String targetFilename, File tempFile) {
        try {
            File fileToCreate = new File(targetFolder.toString(), targetFilename);

            if (!fileToCreate.exists()) {
                LOG.error("File " + targetFilename + " doesn't exist in target path " + targetFolder + ".  Cannot update one here.");
                return serviceFailure(notFoundError(File.class));
            }

            Path targetFile = Files.copy(tempFile.toPath(), Paths.get(targetFolder.toString(), targetFilename), StandardCopyOption.REPLACE_EXISTING);
            return serviceSuccess(targetFile.toFile());
        } catch (IOException e) {
            LOG.error("Unable to copy temporary file " + tempFile + " to target folder " + targetFolder + " and file " + targetFilename, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_UPDATE_FILE));
        }
    }

    private ServiceResult<Path> createFolders(Path path) {
        try {
            return serviceSuccess(Files.createDirectories(path));
        } catch (IOException e) {
            LOG.error("Error creating folders " + path, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FOLDERS));
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
