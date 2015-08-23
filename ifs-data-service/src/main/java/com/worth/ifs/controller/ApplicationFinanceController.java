package com.worth.ifs.controller;

import com.worth.ifs.assembler.ApplicationFinanceResourceAssembler;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.repository.ApplicationFinanceRepository;
import com.worth.ifs.resource.ApplicationFinanceResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    ApplicationFinanceResourceAssembler applicationFinanenceResourceAssember;

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public ApplicationFinanceResource findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        ApplicationFinanceResource applicationFinanceResource = applicationFinanenceResourceAssember.toResource(applicationFinance);
        return applicationFinanceResource;
    }

}
