package org.innovateuk.ifs.project.financecheck;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Override
    public FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key){
        return financeCheckRestService.getByProjectAndOrganisation(key.getProjectId(), key.getOrganisationId()).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> update(FinanceCheckResource toUpdate){
        return financeCheckRestService.update(toUpdate).toServiceResult();
    }

    @Override
    public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        return financeCheckRestService.getFinanceCheckSummary(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveFinanceCheck(Long projectId, Long organisationId) {
        return financeCheckRestService.approveFinanceCheck(projectId, organisationId).toServiceResult();
    }

    @Override
    public FinanceCheckProcessResource getFinanceCheckApprovalStatus(Long projectId, Long organisationId) {
        return financeCheckRestService.getFinanceCheckApprovalStatus(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public FinanceCheckEligibilityResource getFinanceCheckEligibility(Long projectId, Long organisationId) {
        return financeCheckRestService.getFinanceCheckEligibility(projectId, organisationId).getSuccessObjectOrThrowException();
    }
}
