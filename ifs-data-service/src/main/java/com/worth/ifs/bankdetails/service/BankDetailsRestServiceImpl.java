package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class BankDetailsRestServiceImpl extends BaseRestService implements BankDetailsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<BankDetailsResource> getByProjectIdAndBankDetailsId(final Long projectId, final Long bankDetailsId){
        return getWithRestResult(projectRestURL + "/" + projectId + "/bank-details?bankDetailsId=" + bankDetailsId, BankDetailsResource.class);
    }

    @Override
    public RestResult<Void> submitBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource){
        return putWithRestResult(projectRestURL + "/" + projectId + "/bank-details", bankDetailsResource, Void.class);
    }

    @Override
    public RestResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource){
        return postWithRestResult(projectRestURL + "/" + projectId + "/bank-details", bankDetailsResource, Void.class);
    }

    @Override
    public RestResult<BankDetailsResource> getBankDetailsByProjectAndOrganisation(Long projectId, Long organisationId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/bank-details?organisationId=" + organisationId, BankDetailsResource.class);
    }

    @Override
    public RestResult<ProjectBankDetailsStatusSummary> getBankDetailsStatusSummaryByProject(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/bank-details/status-summary", ProjectBankDetailsStatusSummary.class);
    }
}
