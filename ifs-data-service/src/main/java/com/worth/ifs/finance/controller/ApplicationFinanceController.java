package com.worth.ifs.finance.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public ApplicationFinance findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        return applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
    }

    @RequestMapping("/findByApplication/{applicationId}")
    public List<ApplicationFinance> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {
        return applicationFinanceRepository.findByApplicationId(applicationId);
    }

    @RequestMapping("/add/{applicationId}/{organisationId}")
    public ApplicationFinance add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        Application application = applicationRepository.findById(applicationId);
        Organisation organisation = organisationRepository.findById(organisationId);
        ApplicationFinance applicationFinance = new ApplicationFinance(application, organisation);
        return applicationFinanceRepository.save(applicationFinance);
    }
}
