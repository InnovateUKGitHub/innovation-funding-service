package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for handling the overhead calculation file upload with permission definitions
 */
public interface OverheadFileService {

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'CREATE_OVERHEAD_FILE')")
    ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'READ_OVERHEAD_CONTENTS')")
    ServiceResult<FileAndContents> getFileEntryContents(long overheadId);

    @SecuredBySpring(value = "PROJECT_OVERHEAD_CALCULATION_FILE", description = "Project finance users can access overhead calucation spreadsheet file for any project")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    ServiceResult<FileAndContents> getProjectFileEntryContents(long overheadId);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'READ_OVERHEAD_DETAILS')")
    ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId);

    @SecuredBySpring(value = "PROJECT_OVERHEAD_CALCULATION_FILE_DETAILS", description = "Project finance users can access overhead calucation spreadsheet file details for any project")
    @PreAuthorize("hasAnyAuthority('project_finance', 'external_finance')")
    ServiceResult<FileEntryResource> getProjectFileEntryDetails(long overheadId);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'UPDATE_OVERHEAD_FILE')")
    ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#overheadId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'DELETE_OVERHEAD_FILE')")
    ServiceResult<Void> deleteFileEntry(long overheadId);
}