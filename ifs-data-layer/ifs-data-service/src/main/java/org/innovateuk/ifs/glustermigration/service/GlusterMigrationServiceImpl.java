package org.innovateuk.ifs.glustermigration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.glustermigration.GlusterMigrationStatusType;
import org.innovateuk.ifs.glustermigration.domain.GlusterMigrationStatus;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.glustermigration.repository.GlusterMigrationStatusRepository;
import org.innovateuk.ifs.file.transactional.gluster.FileStorageStrategy;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlusterMigrationServiceImpl implements GlusterMigrationService {

    @Autowired
    private GlusterMigrationStatusRepository glusterMigrationStatusRepository;
    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    @Qualifier("finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;

    @Override
    public ServiceResult<ScheduleResponse> processGlusterFiles() throws IOException {
        log.info("Get files from gluster");
        StopWatch stopWatch = new StopWatch(GlusterMigrationServiceImpl.class.getSimpleName());
        stopWatch.start();
        List<GlusterMigrationStatus> glusterMigrationStatuses = glusterMigrationStatusRepository.findGlusterMigrationStatusByStatusEquals(GlusterMigrationStatusType.FILE_NOT_FOUND.toString());
        List<Long> fileIds = glusterMigrationStatuses.stream()
                .map(GlusterMigrationStatus::getId)
                .collect(Collectors.toList());
        List<FileEntry> fileEntries = fileEntryRepository.findByNullUUID(PageRequest.of(0, 10), fileIds);
        log.info("Number of files entry retrieved " + fileEntries.size());
        for (FileEntry fileEntry : fileEntries) {
            log.info(fileEntry.toString());
            ServiceResult<File> result = finalFileStorageStrategy.getFile(fileEntry).andOnFailure(() -> scannedFileStorageStrategy.getFile(fileEntry));
            log.info("file retrieval result " + result.isSuccess());
            if (result.isSuccess()) {
                log.info("file entry to process " + fileEntry.getId());
                File file = result.getSuccess();
                FileUploadRequest.FileUploadRequestBuilder fileUploadRequestBuilder = FileUploadRequestBuilder.fromResource(FileUtils.readFileToByteArray(file),
                        MediaType.valueOf(fileEntry.getMediaType()),
                        fileEntry.getName());
                ResponseEntity<FileUploadResponse> fileUploadResponseEntity = fileUpload.fileUpload(fileUploadRequestBuilder.build());
                if (fileUploadResponseEntity.getBody() != null) {
                    fileEntry.setFileUuid(fileUploadResponseEntity.getBody().getFileId());
                    fileEntryRepository.save(fileEntry);
                }
            } else {
                log.info("No files retrieved from gluster");
                glusterMigrationStatusRepository.save(new GlusterMigrationStatus(fileEntry.getId(), GlusterMigrationStatusType.FILE_NOT_FOUND.toString(), ""));
            }

        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return ServiceResult.serviceSuccess(ScheduleResponse.noWorkNeeded());
    }
}
