package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link ApplicationFinance} related data.
 */
public interface ApplicationFinanceRestService {
    RestResult<ApplicationFinanceResource> getApplicationFinance(Long applicationId, Long organisationId);
    RestResult<List<ApplicationFinanceResource>> getApplicationFinances(Long applicationId);
    RestResult<ApplicationFinanceResource> addApplicationFinanceForOrganisation(Long applicationId, Long organisationId);
    RestResult<ApplicationFinanceResource> update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance);
    RestResult<ApplicationFinanceResource> getById(Long applicationFinanceId);
    RestResult<Double> getResearchParticipationPercentage(Long applicationId);
    RestResult<ApplicationFinanceResource> getFinanceDetails(Long applicationId, Long organisationId);
    RestResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId);
    RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFinanceDocument(Long applicationFinanceId);
    RestResult<ByteArrayResource> getFile(Long applicationFinanceId);
    RestResult<FileEntryResource> getFileDetails(Long applicationFinanceId);
}
