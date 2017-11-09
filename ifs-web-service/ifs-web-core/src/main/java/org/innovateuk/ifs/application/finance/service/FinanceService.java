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
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationFinanceResource getApplicationFinanceByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationFinanceResource getApplicationFinanceDetails( Long userId, Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId, Long organisationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ApplicationFinanceResource> getApplicationFinanceDetails(Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    RestResult<Void> removeFinanceDocument(Long applicationFinanceId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceFileEntryId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    RestResult<FileEntryResource> getFinanceEntryByApplicationFinanceId(Long applicationFinanceId);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId);
}
