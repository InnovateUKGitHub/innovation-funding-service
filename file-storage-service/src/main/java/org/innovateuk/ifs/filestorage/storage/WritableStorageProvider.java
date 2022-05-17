package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.exception.ServiceException;

import java.io.IOException;

public interface WritableStorageProvider {

    String saveFile(FileUploadRequest fileUploadRequest) throws ServiceException;

}

