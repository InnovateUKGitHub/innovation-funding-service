package org.innovateuk.ifs.project.bankdetails.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.bankDetailsReviewResourceListType;

@Service
public class BankDetailsRestServiceImpl extends BaseRestService implements BankDetailsRestService {
    
    private String competitionRestURL = "/competition";
    private String competitionsRestURL = "/competitions";
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
        String url = competitionRestURL + "/" + competitionId + "/bank-details/export";
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals() {
        return getWithRestResult(competitionsRestURL + "/pending-bank-details-approvals", bankDetailsReviewResourceListType());
    }

    @Override
    public RestResult<Long> countPendingBankDetailsApprovals() {
        return getWithRestResult(competitionsRestURL + "/count-pending-bank-details-approvals", Long.class);
    }
}
