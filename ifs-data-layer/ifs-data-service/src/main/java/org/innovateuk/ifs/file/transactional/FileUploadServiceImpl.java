package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.function.Supplier;

@Service
public class FileUploadServiceImpl extends BaseTransactionalService implements FileUploadService {

    @Override
    public ServiceResult<FileEntryResource> createFileEntry(long uploadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return null;
    }

    @Override
    public ServiceResult<Void> deleteFileEntry(long uploadId) {
        return null;
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long uploadId) {
        return null;
    }
}
