package org.innovateuk.ifs.file.transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.io.File.separator;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;
import static org.innovateuk.ifs.file.util.FileFunctions.pathElementsToFile;
import static org.innovateuk.ifs.file.util.FileFunctions.pathElementsToPath;

/**
 * Represents a component that, given a FileEntry to store, decides how best to store it and stores it on the filesystem.
 * This base class is for a storage mechanism that expects to have a pointer to a base folder that all storage is performed
 * within, as well as a containing folder within that storage base that this strategy will put all files into (and create
 * if it doesn't yet exist)
 */
@Slf4j
abstract class BaseFileStorageStrategy implements FileStorageStrategy {

    private static final String FOR_FILE_ENTRY_WITH_ID = " for fileEntry with id ";
    private static final String AND_FILE = " and file ";
    private static final String TO_TARGET_FOLDER = " to target folder ";
    private static final String UNABLE_TO_MOVE_TEMPORARY_FILE = "Unable to move temporary file ";

    protected String pathToStorageBase;
    protected String containingFolder;

    public BaseFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        this.pathToStorageBase = pathToStorageBase;
        this.containingFolder = containingFolder;
    }

    @Override
    public Pair<List<String>, String> getAbsoluteFilePathAndName(FileEntry file) {
        return getAbsoluteFilePathAndName(file.getId());
    }

    @Override
    public boolean exists(FileEntry file) {
        return getFile(file).isSuccess();
    }

    @Override
    public ServiceResult<File> getFile(FileEntry file) {
        Pair<List<String>, String> absoluteFilePathAndName = getAbsoluteFilePathAndName(file);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();
        Path foldersPath = pathElementsToPath(pathElements);
        File fileOnFilesystem = new File(foldersPath.toFile(), filename);

        if (fileOnFilesystem.exists()) {
            return serviceSuccess(fileOnFilesystem);
        } else {
            return serviceFailure(notFoundError(FileEntry.class, file.getId()));
        }
    }

    @Override
    public ServiceResult<File> createFile(FileEntry fileEntry, File temporaryFile) {
        Pair<List<String>, String> absoluteFilePathAndName = getAbsoluteFilePathAndName(fileEntry);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();
        log.info("[Filelogging] Creating file with FileEntry ID " + fileEntry.getId() + " and size: " + fileEntry.getFilesizeBytes());
        return createFileForFileEntry(pathElements, filename, temporaryFile);
    }

    @Override
    public ServiceResult<File> updateFile(FileEntry fileEntry, File temporaryFile) {
        Pair<List<String>, String> absoluteFilePathAndName = getAbsoluteFilePathAndName(fileEntry);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();
        return updateFileForFileEntry(pathElements, filename, temporaryFile);
    }

    @Override
    public ServiceResult<File> moveFile(Long fileEntryId, File temporaryFile) {
        final Pair<List<String>, String> absoluteFilePathAndName = getAbsoluteFilePathAndName(fileEntryId);
        final List<String> pathElements = absoluteFilePathAndName.getLeft();
        final String filename = absoluteFilePathAndName.getRight();
        return moveFileForFileEntry(pathElements, filename, temporaryFile);
    }

    @Override
    public ServiceResult<Void> deleteFile(FileEntry fileEntry) {
        Pair<List<String>, String> absoluteFilePathAndName = getAbsoluteFilePathAndName(fileEntry);
        List<String> pathElements = absoluteFilePathAndName.getLeft();
        String filename = absoluteFilePathAndName.getRight();
        File filePath = pathElementsToFile(combineLists(pathElements, filename));

        log.info("[Filelogging] Deleting file " + filePath.getAbsolutePath()  + FOR_FILE_ENTRY_WITH_ID + fileEntry.getId());

        if (filePath.delete()) {
            log.info("[Filelogging] File " + filename + FOR_FILE_ENTRY_WITH_ID + fileEntry.getId() + " deleted successfully");
            return serviceSuccess();
        } else {
            log.info("[Filelogging] Cannot delete file " + filePath.getAbsolutePath() + FOR_FILE_ENTRY_WITH_ID + fileEntry.getId());
            return serviceFailure(new Error(FILES_UNABLE_TO_DELETE_FILE, FileEntry.class, fileEntry.getId()));
        }
    }

    @Override
    public List<Pair<Long, Pair<List<String>, String>>> allWithIds() {
        return all().stream().map(path -> Pair.of(fileEntryIdFromPath(path).getSuccess(), path)).collect(toList());
    }

    private ServiceResult<File> createFileForFileEntry(List<String> absolutePathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToPath(absolutePathElements);

        return createFolders(foldersPath).
                andOnSuccess(createdFolders -> copyTempFileToTargetFile(createdFolders, filename, tempFile));
    }

    private ServiceResult<File> moveFileForFileEntry(List<String> absolutePathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToPath(absolutePathElements);

        return createFolders(foldersPath).
                andOnSuccess(createdFolders -> moveTempFileToTargetFile(createdFolders, filename, tempFile));
    }

    private ServiceResult<Path> createFolders(Path path) {
        try {
            return serviceSuccess(Files.createDirectories(path));
        } catch (IOException e) {
            log.error(" [Filelogging] Error creating folders " + path, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FOLDERS));
        }
    }

    private ServiceResult<File> copyTempFileToTargetFile(Path targetFolder, String targetFilename, File tempFile) {
        try {
            log.info("[Filelogging]  Copying temp file from " + tempFile.getAbsolutePath() + " to target directory " + targetFolder + " with targetFilename " + targetFilename);

            File fileToCreate = new File(targetFolder.toString(), targetFilename);

            if (fileToCreate.exists()) {
                log.error("[Filelogging]  File " + targetFilename + " already existed in target path " + targetFolder + ".  Cannot create a new one here.");
                return serviceFailure(new Error(FILES_DUPLICATE_FILE_CREATED));
            }

            Path targetFile = Files.copy(tempFile.toPath(), Paths.get(targetFolder.toString(), targetFilename));
            log.info("[Filelogging] Path of file created: " + targetFile.toAbsolutePath());
            return serviceSuccess(targetFile.toFile());
        } catch (IOException e) {
            log.error("[Filelogging] Unable to copy temporary file " + tempFile + TO_TARGET_FOLDER + targetFolder + AND_FILE + targetFilename, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }
    }


    private ServiceResult<File> moveTempFileToTargetFile(Path targetFolder, String targetFilename, File tempFile) {
        File fileToCreate = null;
        try {
            fileToCreate = new File(targetFolder.toString(), targetFilename);
            final Path targetFile = Files.move(tempFile.toPath(), fileToCreate.toPath());
            return serviceSuccess(targetFile.toFile());
        } catch (final FileAlreadyExistsException e) {
            log.trace("file already exists", e);
            if (!tempFile.exists()) {
                // Move might already have happened as we do not have file to copy and the target already exists
                return serviceFailure(new Error(FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT));
            }
            log.error(UNABLE_TO_MOVE_TEMPORARY_FILE + tempFile + TO_TARGET_FOLDER + targetFolder + AND_FILE + targetFilename + " file already exists");
            return serviceFailure(new Error(FILES_DUPLICATE_FILE_MOVED));
        } catch (final NoSuchFileException e) {
            log.trace("no such file", e);
            if (fileToCreate != null && fileToCreate.exists()) {
                // Move might already have happened as we do not have file to copy and the target already exists
                return serviceFailure(new Error(FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT));
            }
            log.error(UNABLE_TO_MOVE_TEMPORARY_FILE + tempFile + TO_TARGET_FOLDER + targetFolder + AND_FILE + targetFilename + "file does not exist");
            return serviceFailure(new Error(FILES_NO_SUCH_FILE));
        } catch (final IOException e) {
            log.error(UNABLE_TO_MOVE_TEMPORARY_FILE + tempFile + TO_TARGET_FOLDER + targetFolder + AND_FILE + targetFilename, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_MOVE_FILE));
        }
    }


    private ServiceResult<File> updateFileForFileEntry(List<String> absolutePathElements, String filename, File tempFile) {

        Path foldersPath = pathElementsToPath(absolutePathElements);
        return updateExistingFileWithTempFile(foldersPath, filename, tempFile);
    }


    private ServiceResult<File> updateExistingFileWithTempFile(Path targetFolder, String targetFilename, File tempFile) {
        try {
            File fileToCreate = new File(targetFolder.toString(), targetFilename);

            if (!fileToCreate.exists()) {
                log.error("File " + targetFilename + " doesn't exist in target path " + targetFolder + ".  Cannot update one here.");
                return serviceFailure(notFoundError(FileEntry.class, targetFilename));
            }

            Path targetFile = Files.copy(tempFile.toPath(), Paths.get(targetFolder.toString(), targetFilename), StandardCopyOption.REPLACE_EXISTING);
            return serviceSuccess(targetFile.toFile());
        } catch (IOException e) {
            log.error("Unable to copy temporary file " + tempFile + TO_TARGET_FOLDER + targetFolder + AND_FILE + targetFilename, e);
            return serviceFailure(new Error(FILES_UNABLE_TO_UPDATE_FILE));
        }
    }


    protected List<String> getAbsolutePathToFileUploadFolder() {
        return getAbsolutePathToFileUploadFolder(separator);
    }

    List<String> getAbsolutePathToFileUploadFolder(String fileSeparator) {

        // get an absolute path to the file upload folder, split into segments
        String separatorToUseInSplit = separatorForSplit(fileSeparator);
        List<String> pathToStorageBaseAsSegments = asList(pathToStorageBase.split(separatorToUseInSplit));
        List<String> fullPathToContainingFolderWithEmptyElements = combineLists(pathToStorageBaseAsSegments, containingFolder);
        List<String> fullPathWithNoEmptyElements = simpleFilterNot(fullPathToContainingFolderWithEmptyElements, StringUtils::isBlank);

        // then if using a *nix path that starts with file separator e.g. /tmp/path, ensure that we retain the leading "/"
        if (pathToStorageBase.startsWith(fileSeparator)) {
            fullPathWithNoEmptyElements.set(0, fileSeparator + fullPathWithNoEmptyElements.get(0));
        }

        return fullPathWithNoEmptyElements;
    }

    private String separatorForSplit(String fileSeparator) {
        if ("\\".equals(fileSeparator)) {
            return "\\\\";
        }
        return fileSeparator;
    }
}