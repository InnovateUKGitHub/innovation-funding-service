package org.innovateuk.ifs.glustermigration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.IfsConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.transactional.gluster.FileStorageStrategy;
import org.innovateuk.ifs.file.transactional.gluster.GlusterFileServiceImpl;
import org.innovateuk.ifs.glustermigration.GlusterMigrationStatusType;
import org.innovateuk.ifs.glustermigration.domain.GlusterMigrationStatus;
import org.innovateuk.ifs.glustermigration.repository.GlusterMigrationStatusRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlusterMigrationServiceImpl implements GlusterMigrationService {

    @Value("${ifs.data.service.gluster.file.migration.millis:50}")
    private Integer fileEntryBatch;

    @Autowired
    private GlusterMigrationStatusRepository glusterMigrationStatusRepository;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private GlusterFileServiceImpl glusterFileService;

    @Override
    @Transactional
    public ServiceResult<ScheduleResponse> processGlusterFiles() throws IOException {
        StopWatch stopWatch = new StopWatch(GlusterMigrationServiceImpl.class.getSimpleName());
        stopWatch.start();
        List<GlusterMigrationStatus> glusterMigrationStatuses = glusterMigrationStatusRepository.findGlusterMigrationStatusByStatusEquals(GlusterMigrationStatusType.FILE_NOT_FOUND.toString());
        List<Long> fileEntryIds = glusterMigrationStatuses.stream()
                .map(GlusterMigrationStatus::getFileEntryId)
                .collect(Collectors.toList());
        List<FileEntry> fileEntries = getFileEntries(fileEntryIds);
        log.info("Number of files entry retrieved " + fileEntries.size());
        for (FileEntry fileEntry : fileEntries) {
            ServiceResult<Pair<File, FileStorageStrategy>> result = glusterFileService.findFileForGet(fileEntry);
            log.info("file retrieval result for file entry {} is {}", fileEntry.getId(), result.isSuccess());
            if (result.isSuccess()) {
                Pair<File, FileStorageStrategy> file = result.getSuccess();
                UUID fileUuid = UUID.randomUUID();
                FileUploadRequest.FileUploadRequestBuilder fileUploadRequestBuilder = getFileUploadRequestBuilder(fileEntry, file.getKey(), fileUuid);
                ResponseEntity<FileUploadResponse> fileUploadResponseEntity = fileUpload.fileUpload(fileUploadRequestBuilder.build());
                if (fileUploadResponseEntity.getStatusCode().is2xxSuccessful()) {
                    fileEntry.setFileUuid(fileUuid.toString());
                    fileEntry.setMd5Checksum(fileUploadResponseEntity.getBody().getMd5Checksum());
                    fileEntryRepository.save(fileEntry);
                    glusterMigrationStatusRepository.save(new GlusterMigrationStatus(null, fileEntry.getId(), GlusterMigrationStatusType.FILE_FOUND.toString(), ""));

                } else {
                    glusterMigrationStatusRepository.save(new GlusterMigrationStatus(null, fileEntry.getId(), GlusterMigrationStatusType.FILE_PROCESS_ERROR.toString(), fileUploadResponseEntity.getStatusCode().getReasonPhrase()));
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

    private FileUploadRequest.FileUploadRequestBuilder getFileUploadRequestBuilder(FileEntry fileEntry, File file, UUID fileId) throws IOException {
        return FileUploadRequest.builder()
                .fileId(fileId.toString())
                .fileName(fileEntry.getName())
                .md5Checksum(FileHashing.fileHash64(FileUtils.readFileToByteArray(file)))
                .mimeType(fileEntry.getMediaType())
                .payload(FileUtils.readFileToByteArray(file))
                .userId(IfsConstants.IFS_SYSTEM_USER)
                .fileSizeBytes(FileUtils.readFileToByteArray(file).length)
                .systemId(IfsConstants.IFS_SYSTEM_USER);
    }

    private List<FileEntry> getFileEntries(List<Long> fileEntryIds) {
        return Optional.of(fileEntryRepository.findFileEntryByIdNotInAndFileUuidIsNull(fileEntryIds, PageRequest.of(0, fileEntryBatch)))
                .orElse(fileEntryRepository.findFileEntryByFileUuidIsNull(PageRequest.of(0, fileEntryBatch)));
    }
}
