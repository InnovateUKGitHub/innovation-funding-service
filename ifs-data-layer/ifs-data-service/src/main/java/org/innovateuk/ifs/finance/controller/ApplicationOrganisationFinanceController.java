package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.finance.transactional.ApplicationOrganisationFinanceService;
import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A Controller to support the "Your organisation" section of Application Form finances.
 */
@RestController
@RequestMapping("/application/{targetId}/organisation/{organisationId}/finance")
public class ApplicationOrganisationFinanceController extends AbstractOrganisationFinanceController {

    @Autowired
    private ApplicationOrganisationFinanceService applicationOrganisationFinanceService;

    @Override
    protected OrganisationFinanceService getOrganisationFinanceService() {
        return applicationOrganisationFinanceService;
    }
}
