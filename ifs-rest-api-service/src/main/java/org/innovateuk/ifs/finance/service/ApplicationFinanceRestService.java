package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link ApplicationFinanceResource} related data.
 */
public interface ApplicationFinanceRestService {
    RestResult<ApplicationFinanceResource> getApplicationFinance(Long applicationId, Long organisationId);
    RestResult<List<ApplicationFinanceResource>> getApplicationFinances(Long applicationId);
    RestResult<ApplicationFinanceResource> update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance);
    RestResult<ApplicationFinanceResource> getById(Long applicationFinanceId);
    RestResult<Double> getResearchParticipationPercentage(Long applicationId);
    RestResult<ApplicationFinanceResource> getFinanceDetails(Long applicationId, Long organisationId);
    RestResult<List<ApplicationFinanceResource>> getFinanceDetails(Long applicationId);
    RestResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId);
    RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFinanceDocument(Long applicationFinanceId);
    RestResult<ByteArrayResource> getFile(Long applicationFinanceId);
    RestResult<FileEntryResource> getFileDetails(Long applicationFinanceId);
}
