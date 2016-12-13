package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;

import java.util.List;

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

    RestResult<Void> completeSpendProfilesReview(Long projectId);

    RestResult<List<ProjectFinanceResource>> getFinanceTotals(Long projectId);

    RestResult<Viability> getViability(Long projectId, Long organisationId);

    RestResult<Void> saveViability(Long projectId, Long organisationId, Viability viability);
}
