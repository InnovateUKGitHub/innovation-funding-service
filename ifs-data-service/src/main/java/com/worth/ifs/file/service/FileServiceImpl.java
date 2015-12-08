package com.worth.ifs.file.service;

import com.worth.ifs.file.domain.BaseFile;
import com.worth.ifs.file.resource.BaseFileConverter;
import com.worth.ifs.file.resource.BaseFileResource;
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

import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.UNABLE_TO_CREATE_FILE;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.copyOfRange;

/**
 * The class is an implementation of FileService that, based upon a given strategy, is able to
 * store and retrieve files.
 */
public class FileServiceImpl extends BaseTransactionalService implements FileService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_FILE
    }

    @Autowired
    private FileStorageStrategy strategy;

    @Autowired
    private BaseFileConverter converter;

    @Override
    public Either<ServiceFailure, ServiceSuccess<File>> createFile(BaseFileResource resource) {

        return handlingErrors(() -> {

            BaseFile baseFile = converter.valueOf(resource);
            BaseFile savedFileEntry = converter.save(baseFile);

            Pair<List<String>, String> filePathAndName = strategy.getAbsoluteFilePathAndName(savedFileEntry);
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
