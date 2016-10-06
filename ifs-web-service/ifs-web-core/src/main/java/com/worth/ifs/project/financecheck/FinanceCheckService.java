package com.worth.ifs.project.financecheck;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;

public interface FinanceCheckService {

    FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    ServiceResult<Void> update(FinanceCheckResource toUpdate);

    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    ServiceResult<Void> approveFinanceCheck(Long projectId, Long organisationId);

    FinanceCheckProcessResource getFinanceCheckApprovalStatus(Long projectId, Long organisationId);
}
