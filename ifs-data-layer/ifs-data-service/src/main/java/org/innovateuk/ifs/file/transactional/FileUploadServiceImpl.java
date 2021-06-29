package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class FileUploadServiceImpl extends BaseTransactionalService implements FileUploadService {

    @Autowired
    private BuildDataFromFile buildDataFromFile;

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> uploadFile(String uploadFileType, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        buildDataFromFile.buildFromFile(inputStreamSupplier.get());
        return serviceSuccess(new FileEntryResource());
    }
}
