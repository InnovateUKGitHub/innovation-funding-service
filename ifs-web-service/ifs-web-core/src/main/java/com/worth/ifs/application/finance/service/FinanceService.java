package com.worth.ifs.application.finance.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId);
    ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId);
    ApplicationFinanceResource getApplicationFinanceByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    ApplicationFinanceResource getApplicationFinanceDetails( Long userId, Long applicationId);
    List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
    RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFinanceDocument(Long applicationFinanceId);
    RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceFileEntryId);
    RestResult<FileEntryResource> getFinanceEntryByApplicationFinanceId(Long applicationFinanceId);
    RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId);
}
