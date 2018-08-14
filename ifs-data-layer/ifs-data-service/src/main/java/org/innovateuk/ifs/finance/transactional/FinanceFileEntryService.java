package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

public interface FinanceFileEntryService {
    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'CREATE_FILE_ENTRY')")
    ServiceResult<FileEntryResource> createFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_FILE_ENTRY')")
    ServiceResult<FileEntryResource> updateFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'DELETE_FILE_ENTRY')")
    ServiceResult<Void> deleteFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileAndContents> getFileContents(@P("applicationFinanceId")long applicationFinanceId);
}
