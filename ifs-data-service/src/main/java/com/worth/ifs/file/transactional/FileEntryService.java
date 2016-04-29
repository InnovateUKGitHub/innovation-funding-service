package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.NotSecured;

public interface FileEntryService {

    @NotSecured("TODO")
    ServiceResult<FileEntryResource> findOne(Long id);

    @NotSecured("TODO")
    ServiceResult<FileEntryResource> getFileEntryIdByApplicationFinanceId(Long applicationFinanceId);
}