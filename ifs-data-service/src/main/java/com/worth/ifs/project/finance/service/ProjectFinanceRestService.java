package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface ProjectFinanceRestService {

    RestResult<Void> generateSpendProfile(Long projectId);

    RestResult<Void> acceptOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    RestResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId);

    RestResult<SpendProfileTableResource> getSpendProfileTable(Long projectId, Long organisationId);

    RestResult<SpendProfileCSVResource> getSpendProfileCSV(Long projectId, Long organisationId);

    RestResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    RestResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table);

    RestResult<Void> markSpendProfile(Long projectId, Long organisationId, Boolean complete);
}
