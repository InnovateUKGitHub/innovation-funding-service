package org.innovateuk.ifs.file.transactional.gluster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Slf4j
public class GlusterFileServiceImpl extends RootTransactionalService {

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

    public ServiceResult<FileEntry> findFileEntry(Long fileEntryId) {
        return find(fileEntryRepository.findById(fileEntryId), notFoundError(FileEntry.class, fileEntryId));
    }

    public ServiceResult<Pair<File, FileStorageStrategy>> findFileForGet(FileEntry fileEntry) {
        return findFileInSafeLocation(fileEntry);
    }

    private ServiceResult<Pair<File, FileStorageStrategy>> findFileInSafeLocation(FileEntry fileEntry) {
        return findFileInFinalFileStorageLocation(fileEntry).andOnFailure(() -> findFileInScannedStorageLocation(fileEntry));
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

    private ServiceResult<File> findFileInStorageLocation(FileEntry fileEntry, FileStorageStrategy fileStorageStrategy) {
        return fileStorageStrategy.getFile(fileEntry);
    }

    public ServiceResult<Supplier<InputStream>> getInputStreamSupplier(File file) {
        return serviceSuccess(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                log.error("Unable to supply FileInputStream for file " + file, e);
                throw new IllegalStateException("Unable to supply FileInputStream for file " + file, e);
            }
        });
    }


}
