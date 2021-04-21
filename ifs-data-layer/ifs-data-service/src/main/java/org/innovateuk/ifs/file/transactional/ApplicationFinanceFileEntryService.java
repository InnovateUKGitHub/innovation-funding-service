package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;


public interface ApplicationFinanceFileEntryService {

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileEntryResource> getFileEntryByApplicationFinanceId(@P("applicationFinanceResourceId") Long applicationFinanceId);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileEntryResource> getFECCertificateFileEntryByApplicationFinanceId(@P("applicationFinanceResourceId") Long applicationFinanceId);
}