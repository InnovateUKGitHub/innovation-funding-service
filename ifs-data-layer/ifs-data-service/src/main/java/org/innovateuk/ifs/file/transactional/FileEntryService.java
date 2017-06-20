package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FileEntryService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> findOne(Long id);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileEntryResource> getFileEntryByApplicationFinanceId(@P("applicationFinanceResourceId") Long applicationFinanceId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> saveFile(FileEntryResource newFile);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeFile(Long fileId);

}