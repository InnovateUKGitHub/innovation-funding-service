package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.springframework.stereotype.Service;

/**
 * Rest Service for dealing with Project finance operations
 */
@Service
public class ProjectFinanceRestServiceImpl extends BaseRestService implements ProjectFinanceRestService {

    private String projectFinanceRestURL = "/project";

    @Override
    public RestResult<Void> generateSpendProfile(Long projectId) {
        String url = projectFinanceRestURL + "/" + projectId + "/spend-profile/generate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> acceptOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        return postWithRestResult(projectFinanceRestURL + "/" + projectId + "/spend-profile/approval/" + approvalType, Void.class);
    }

    @Override
    public RestResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId) {
        return getWithRestResult(projectFinanceRestURL + "/" + projectId + "/spend-profile/approval", ApprovalType.class);
    }

    @Override
    public RestResult<SpendProfileTableResource> getSpendProfileTable(Long projectId, Long organisationId) {
        String url = projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-table";
        return getWithRestResult(url, SpendProfileTableResource.class);
    }

    @Override
    public RestResult<SpendProfileCSVResource> getSpendProfileCSV(Long projectId, Long organisationId) {
        String url = projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-csv";
        return getWithRestResult(url, SpendProfileCSVResource.class);
    }

    @Override
    public RestResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId) {
        String url = projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";
        return getWithRestResult(url, SpendProfileResource.class);
    }

    @Override
    public RestResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table) {
        return postWithRestResult(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile", table, Void.class);
    }

    @Override
    public RestResult<Void> markSpendProfile(Long projectId, Long organisationId, Boolean complete) {
        return postWithRestResult(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/complete/" + complete, Void.class);
    }

    @Override
    public RestResult<Void> completeSpendProfilesReview(Long projectId) {
        return postWithRestResult(projectFinanceRestURL + "/" + projectId + "/complete-spend-profiles-review/", Void.class);
    }
}
