package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

/**
 * Interface for CRUD operations on Overhead File Entries.
 */
public interface OverheadFileRestService {

    RestResult<FileEntryResource> addOverheadCalculationFile(Long overheadId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<FileEntryResource> updateOverheadCalculationFile(Long overheadId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<Void> removeOverheadCalculationFile(Long overheadId);

    RestResult<ByteArrayResource> getOverheadFile(Long overheadId);

    RestResult<ByteArrayResource> getOverheadFileUsingProjectFinanceRowId(Long projectFinanceRowId);

    RestResult<FileEntryResource> getOverheadFileDetailsUsingProjectFinanceRowId(Long overheadId);

    RestResult<FileEntryResource> getOverheadFileDetails(Long overheadId);

}
