package com.worth.ifs.finance.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
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

    @Autowired
    CostRepository costRepository;

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

    @RequestMapping("/getResearchParticipationPercentage/{applicationId}")
    public double getResearchParticipationPercentage(
            @PathVariable("applicationId") final Long applicationId) {
        List<ApplicationFinance> finances = applicationFinanceRepository.findByApplicationId(applicationId);
        log.warn(String.format("Finances Size: %s", finances.size()));

        return 0.0;
    }

    @RequestMapping("/add/{applicationId}/{organisationId}")
    public ApplicationFinance add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        Application application = applicationRepository.findOne(applicationId);
        Organisation organisation = organisationRepository.findOne(organisationId);
        ApplicationFinance applicationFinance = new ApplicationFinance(application, organisation);
        return applicationFinanceRepository.save(applicationFinance);
    }

    @RequestMapping("/getById/{applicationFinanceId}")
    public ApplicationFinanceResource findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId){
        return new ApplicationFinanceResource(applicationFinanceRepository.findOne(applicationFinanceId));
    }

    @RequestMapping("/update/{applicationFinanceId}")
    public ApplicationFinanceResource update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance){
        log.error(String.format("ApplicationFinanceController.update(%d)", applicationFinanceId));
        ApplicationFinance dbFinance = applicationFinanceRepository.findOne(applicationFinance.getId());
        dbFinance.merge(applicationFinance);
        dbFinance = applicationFinanceRepository.save(dbFinance);
        return new ApplicationFinanceResource(dbFinance);
    }

    @RequestMapping("/finances/{applicationId}/{organisationId}")
    public ApplicationFinanceResource finances() {
        return null;
    }

    @RequestMapping("/financeTotals/{applicationId}")
    public ApplicationFinanceResource financeTotals() {
        return null;
    }
}
