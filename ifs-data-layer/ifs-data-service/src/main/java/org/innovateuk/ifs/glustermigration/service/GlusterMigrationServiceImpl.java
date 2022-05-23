package org.innovateuk.ifs.glustermigration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.IfsConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.transactional.gluster.FileStorageStrategy;
import org.innovateuk.ifs.glustermigration.GlusterMigrationStatusType;
import org.innovateuk.ifs.glustermigration.domain.GlusterMigrationStatus;
import org.innovateuk.ifs.glustermigration.repository.FileEntryMigrationRepository;
import org.innovateuk.ifs.glustermigration.repository.GlusterMigrationStatusRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlusterMigrationServiceImpl implements GlusterMigrationService {

    @Autowired
    private GlusterMigrationStatusRepository glusterMigrationStatusRepository;

    @Autowired
    private FileEntryMigrationRepository fileEntryMigrationRepository;

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
        List<Long> fileEntryIds = glusterMigrationStatuses.stream()
                .map(GlusterMigrationStatus::getFileEntryId)
                .collect(Collectors.toList());
        List<FileEntry> fileEntries = fileEntryMigrationRepository.findFileEntryByIdNotInAAndFileUuidIsNull(fileEntryIds, PageRequest.of(0, 10));
        log.info("Number of files entry retrieved " + fileEntries.size());
        for (FileEntry fileEntry : fileEntries) {
            log.info("File sequence: " + fileEntry.getId());
            ServiceResult<File> result = finalFileStorageStrategy.getFile(fileEntry).andOnFailure(() -> scannedFileStorageStrategy.getFile(fileEntry));
            log.info("file retrieval result " + result.isSuccess());
            if (result.isSuccess()) {
                log.info("file entry to process " + fileEntry.getId());
                File file = result.getSuccess();
                UUID fileId = UUID.randomUUID();
                FileUploadRequest.FileUploadRequestBuilder fileUploadRequestBuilder = FileUploadRequest.builder()
                        .fileId(fileId.toString())
                        .fileName(fileEntry.getName())
                        .md5Checksum(FileHashing.fileHash64(FileUtils.readFileToByteArray(file)))
                        .mimeType(fileEntry.getMediaType())
                        .payload(FileUtils.readFileToByteArray(file))
                        .userId(IfsConstants.IFS_SYSTEM_USER)
                        .fileSizeBytes(FileUtils.readFileToByteArray(file).length)
                        .systemId(IfsConstants.IFS_SYSTEM_USER);
                ResponseEntity<FileUploadResponse> fileUploadResponseEntity = fileUpload.fileUpload(fileUploadRequestBuilder.build());
                if (fileUploadResponseEntity.getStatusCode().is2xxSuccessful()) {
                    fileEntry.setFileUuid(fileId.toString());
                    fileEntryMigrationRepository.save(fileEntry);
                }

            } else {
                log.info("No files retrieved from gluster");
                glusterMigrationStatusRepository.save(new GlusterMigrationStatus(null, fileEntry.getId(), GlusterMigrationStatusType.FILE_NOT_FOUND.toString(), ""));
            }

        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return ServiceResult.serviceSuccess(ScheduleResponse.noWorkNeeded());
    }
}
