package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.service.ProjectFinanceRestService;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with a Project's finance operations
 */
@Service
public class ProjectFinanceServiceImpl implements ProjectFinanceService {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {
        return projectFinanceRestService.generateSpendProfile(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        return projectFinanceRestService.acceptOrRejectSpendProfile(projectId, approvalType).toServiceResult();
    }

    @Override
    public ApprovalType getSpendProfileStatusByProjectId(Long projectId) {
        return projectFinanceRestService.getSpendProfileStatusByProjectId(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId) {
        return projectFinanceRestService.getSpendProfileTable(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId) {
        return projectFinanceRestService.getSpendProfileCSV(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId) {
        return projectFinanceRestService.getSpendProfile(projectId, organisationId).toOptionalIfNotFound().getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table) {
        return projectFinanceRestService.saveSpendProfile(projectId, organisationId, table).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markSpendProfile(Long projectId, Long organisationId, Boolean complete) {
        return projectFinanceRestService.markSpendProfile(projectId, organisationId, complete).toServiceResult();
    }
}
