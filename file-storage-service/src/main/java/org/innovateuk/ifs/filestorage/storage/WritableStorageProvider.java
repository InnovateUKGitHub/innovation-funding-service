package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.exception.ServiceException;

public interface WritableStorageProvider {

    String saveFile(FileUploadRequest fileUploadRequest) throws ServiceException;

    String deleteFile(FileDeletionRequest fileDeletionRequest);
}

