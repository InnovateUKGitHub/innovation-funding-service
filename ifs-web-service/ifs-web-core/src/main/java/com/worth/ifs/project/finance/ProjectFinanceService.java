package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;

import java.util.Optional;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    ServiceResult<Void> generateSpendProfile(Long projectId);

    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    ApprovalType getSpendProfileStatusByProjectId(Long projectId);

    Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId);

    SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId);

    ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table);

    ServiceResult<Void> markSpendProfile(Long projectId, Long organisationId, Boolean complete);
}
