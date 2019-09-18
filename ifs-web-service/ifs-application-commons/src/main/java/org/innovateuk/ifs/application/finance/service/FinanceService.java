package org.innovateuk.ifs.application.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId);
    ApplicationFinanceResource getApplicationFinanceByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    ApplicationFinanceResource getApplicationFinanceDetails( Long userId, Long applicationId);
    ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId, Long organisationId);
    List<ApplicationFinanceResource> getApplicationFinanceDetails(Long applicationId);
    List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceFileEntryId);
    RestResult<FileEntryResource> getFinanceEntryByApplicationFinanceId(Long applicationFinanceId);
    RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId);
}
