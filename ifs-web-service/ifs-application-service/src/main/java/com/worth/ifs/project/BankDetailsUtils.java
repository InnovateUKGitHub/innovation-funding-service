package com.worth.ifs.project;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.rest.RestResult;

public class BankDetailsUtils {
    public static RestResult<BankDetailsResource> getBankDetails(Long projectId, Long organisationId, BankDetailsRestService bankDetailsRestService) {
        return bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationId);
    }
}
