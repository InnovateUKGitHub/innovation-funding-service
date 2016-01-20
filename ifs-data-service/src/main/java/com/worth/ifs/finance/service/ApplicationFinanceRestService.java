package com.worth.ifs.finance.service;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link ApplicationFinance} related data.
 */
public interface ApplicationFinanceRestService {
    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long organisationId);
    public List<ApplicationFinanceResource> getApplicationFinances(Long applicationId);
    public ApplicationFinanceResource addApplicationFinanceForOrganisation(Long applicationId, Long organisationId);
    public ApplicationFinanceResource update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance);
    public ApplicationFinanceResource getById(Long applicationFinanceId);
    public ApplicationFinanceResource getFinanceDetails(Long applicationId, Long organisationId);
    public List<ApplicationFinanceResource> getFinanceTotals(Long applicationId);
}
