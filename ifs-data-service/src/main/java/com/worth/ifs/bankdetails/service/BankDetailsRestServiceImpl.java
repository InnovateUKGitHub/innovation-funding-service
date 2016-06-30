package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class BankDetailsRestServiceImpl extends BaseRestService implements BankDetailsRestService {

    @Override
    public RestResult<BankDetailsResource> getById(final Long projectId, final Long id){
        return getWithRestResult("/project/" + projectId + "/bank-details?bankDetailsId=" + id, BankDetailsResource.class);
    }

    @Override
    public RestResult<Void> updateBankDetails(final Long projectId, final BankDetailsResource bankDetailsResource){
        return postWithRestResult("/project/" + projectId + "/bank-details", bankDetailsResource, Void.class);
    }

    @Override
    public RestResult<BankDetailsResource> getBankDetailsByProjectAndOrganisation(Long projectId, Long organisationId) {
        return getWithRestResult("/project/" + projectId + "/bank-details?organisationId=" + organisationId, BankDetailsResource.class);
    }
}
