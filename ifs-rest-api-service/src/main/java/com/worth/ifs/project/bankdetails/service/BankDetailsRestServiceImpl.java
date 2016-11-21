package com.worth.ifs.project.bankdetails.service;

import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class BankDetailsRestServiceImpl extends BaseRestService implements BankDetailsRestService {
    
    private String competitionsRestURL = "/competition";
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
    
    @Override
    public RestResult<ByteArrayResource> downloadByCompetition(Long competitionId) {
        String url = competitionsRestURL + "/" + competitionId + "/bank-details/export";
        return getWithRestResult(url, ByteArrayResource.class);
    }
}
