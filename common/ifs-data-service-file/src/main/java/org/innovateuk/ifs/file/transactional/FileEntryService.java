package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public interface FileEntryService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> saveFile(FileEntryResource newFile);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeFile(Long fileId);

}