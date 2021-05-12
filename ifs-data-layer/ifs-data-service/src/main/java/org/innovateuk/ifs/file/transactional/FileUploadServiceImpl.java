package org.innovateuk.ifs.file.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.domain.UploadFiles;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.UploadFilesRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class FileUploadServiceImpl extends BaseTransactionalService implements FileUploadService {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private UploadFilesRepository uploadFilesRepository;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private BuildDataFromFile buildDataFromFile;

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createFileEntry(String uploadFileType, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        buildDataFromFile.buildFromFile(inputStreamSupplier.get());
        return serviceSuccess(new FileEntryResource());
//
//        return  fileService.createFile(fileEntryResource, inputStreamSupplier).
//            andOnSuccessReturn(fileResults -> linkFileEntryToUploadFile(uploadFileType, fileResults))
//                .andOnSuccessReturn(e -> {
//                    getFileContents(e.getId()).andOnSuccessReturnVoid(i -> ;
//                    return e;
//                });
    }

    private FileEntryResource linkFileEntryToUploadFile(String uploadFileType, Pair< File, FileEntry > fileResults) {
        FileEntry fileEntry = fileResults.getValue();
        UploadFiles uploadFiles = new UploadFiles();
        uploadFiles.setType(uploadFileType);
        uploadFiles.setFileEntry(fileEntry);
        uploadFilesRepository.save(uploadFiles);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    @Override
    public ServiceResult<Void> deleteFileEntry(long uploadId) {
        return null;
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long fileEntryId) {
        return  fileEntryService.findOne(fileEntryId)
                .andOnSuccess(fileEntry ->fileService.getFileByFileEntryId(fileEntryId)
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream)));
    }

    @Override
    public ServiceResult<List<FileEntryResource>> getAllUploadedFileEntryResources() {
        Iterable<UploadFiles> uploaddFiles = uploadFilesRepository.findAll();
        List<FileEntryResource> uploadedfFileEntryResources = new ArrayList<FileEntryResource>();
        uploaddFiles.forEach(uploadedFile -> {
            FileEntryResource fileEntryResource = fileEntryMapper.mapToResource(uploadedFile.getFileEntry());
            uploadedfFileEntryResources.add(fileEntryResource);
        });
        return serviceSuccess(uploadedfFileEntryResources);
    }
}
