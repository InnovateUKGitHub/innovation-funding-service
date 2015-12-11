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
import java.nio.file.Paths;
import java.util.List;

import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.DUPLICATE_FILE_CREATED;
import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.UNABLE_TO_CREATE_FILE;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.copyOfRange;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * store and retrieve files.
 */
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_FILE, //
        DUPLICATE_FILE_CREATED, //
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

        });
    }

    private Either<ServiceFailure, File> doCreateFile(List<String> pathElements, String filename) {

        try {

            String[] remainingPathElements =
                    pathElements.size() > 1 ? copyOfRange(pathElements.toArray(new String[]{}), 1, pathElements.size())
                            : new String[] {};

            Path path = Files.createDirectories(Paths.get(File.separator + pathElements.get(0), remainingPathElements));
            File createdFile = new File(path.toString(), filename);

            if (createdFile.exists()) {
                return errorResponse(DUPLICATE_FILE_CREATED);
            }

            boolean createdSuccessfully = createdFile.createNewFile();

            if (createdSuccessfully) {
                return right(createdFile);
            }

            return errorResponse(UNABLE_TO_CREATE_FILE);

        } catch (IOException e) {
            return errorResponse(UNABLE_TO_CREATE_FILE, e);
        }
    }
}
