package org.innovateuk.ifs.project.finance.service;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;

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

    RestResult<Void> markSpendProfileComplete(Long projectId, Long organisationId);

    RestResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId);

    RestResult<Void> completeSpendProfilesReview(Long projectId);
}
