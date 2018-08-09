package org.innovateuk.ifs.project.spendprofile.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.springframework.stereotype.Service;

@Service
public class SpendProfileRestServiceImpl extends BaseRestService implements SpendProfileRestService {
    private static final String PROJECT_REST_URL = "/project";

    @Override
    public RestResult<Void> generateSpendProfile(Long projectId) {
        String url = PROJECT_REST_URL + "/" + projectId + "/spend-profile/generate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> acceptOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        return postWithRestResult(PROJECT_REST_URL + "/" + projectId + "/spend-profile/approval/" + approvalType, Void.class);
    }

    @Override
    public RestResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId) {
        return getWithRestResult(PROJECT_REST_URL + "/" + projectId + "/spend-profile/approval", ApprovalType.class);
    }

    @Override
    public RestResult<SpendProfileTableResource> getSpendProfileTable(Long projectId, Long organisationId) {
        String url = PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-table";
        return getWithRestResult(url, SpendProfileTableResource.class);
    }

    @Override
    public RestResult<SpendProfileCSVResource> getSpendProfileCSV(Long projectId, Long organisationId) {
        String url = PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-csv";
        return getWithRestResult(url, SpendProfileCSVResource.class);
    }

    @Override
    public RestResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId) {
        String url = PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";
        return getWithRestResult(url, SpendProfileResource.class);
    }

    @Override
    public RestResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table) {
        return postWithRestResult(PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile", table, Void.class);
    }

    @Override
    public RestResult<Void> markSpendProfileComplete(Long projectId, Long organisationId) {
        return postWithRestResult(PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/complete", Void.class);
    }

    @Override
    public RestResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId) {
        return postWithRestResult(PROJECT_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/incomplete", Void.class);
    }

    @Override
    public RestResult<Void> completeSpendProfilesReview(Long projectId) {
        return postWithRestResult(PROJECT_REST_URL + "/" + projectId + "/complete-spend-profiles-review/", Void.class);
    }
}