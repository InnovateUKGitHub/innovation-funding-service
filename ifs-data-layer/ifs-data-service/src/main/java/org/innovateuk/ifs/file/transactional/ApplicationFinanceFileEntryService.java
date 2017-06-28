package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;


 public interface ApplicationFinanceFileEntryService  {

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileEntryResource> getFileEntryByApplicationFinanceId(@P("applicationFinanceResourceId") Long applicationFinanceId);
}