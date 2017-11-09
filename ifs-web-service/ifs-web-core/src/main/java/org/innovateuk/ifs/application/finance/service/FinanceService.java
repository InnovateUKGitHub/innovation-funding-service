package org.innovateuk.ifs.application.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    @NotSecured("Not currently secured")
    ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId);
    @NotSecured("Not currently secured")
    ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId);
    @NotSecured("Not currently secured")
    ApplicationFinanceResource getApplicationFinanceByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    @NotSecured("Not currently secured")
    ApplicationFinanceResource getApplicationFinanceDetails( Long userId, Long applicationId);
    @NotSecured("Not currently secured")
    ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId, Long organisationId);
    @NotSecured("Not currently secured")
    List<ApplicationFinanceResource> getApplicationFinanceDetails(Long applicationId);
    @NotSecured("Not currently secured")
    List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    @NotSecured("Not currently secured")
    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
    @NotSecured("Not currently secured")
    RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file);
    @NotSecured("Not currently secured")
    RestResult<Void> removeFinanceDocument(Long applicationFinanceId);
    @NotSecured("Not currently secured")
    RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceFileEntryId);
    @NotSecured("Not currently secured")
    RestResult<FileEntryResource> getFinanceEntryByApplicationFinanceId(Long applicationFinanceId);
    @NotSecured("Not currently secured")
    RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId);
}
