package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;

import java.util.List;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface ProjectFinanceRestService {

    RestResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId);

    RestResult<ProjectFinanceResource> getProjectFinance(Long projectId, Long organisationId);

    RestResult<Boolean> isCreditReportConfirmed(Long projectId, Long organisationId);

    RestResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    RestResult<List<ProjectFinanceResource>> getFinanceTotals(Long applicationId);

    RestResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(long projectId);



}
