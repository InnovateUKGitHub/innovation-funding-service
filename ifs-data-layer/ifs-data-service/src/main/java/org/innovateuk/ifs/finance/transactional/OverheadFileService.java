package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for handling the overhead calculation file upload with permission definitions
 */
public interface OverheadFileService {

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'CREATE_OVERHEAD_FILE')")
    ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'READ_OVERHEAD_CONTENTS')")
    ServiceResult<FileAndContents> getFileEntryContents(long overheadId);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'READ_OVERHEAD_DETAILS')")
    ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'UPDATE_OVERHEAD_FILE')")
    ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'DELETE_OVERHEAD_FILE')")
    ServiceResult<Void> deleteFileEntry(long overheadId);
}