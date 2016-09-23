package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.commons.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FileEntryService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> findOne(Long id);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileEntryResource> getFileEntryByApplicationFinanceId(@P("applicationFinanceResourceId") Long applicationFinanceId);
}