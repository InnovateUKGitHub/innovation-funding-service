package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectOrganisationFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A Controller to support the "Your organisation" section of Project Form finances.
 */
@RestController
@RequestMapping("/project/{targetId}/organisation/{organisationId}/finance")
public class ProjectOrganisationFinanceController extends AbstractOrganisationFinanceController {

    @Autowired
    private ProjectOrganisationFinanceService projectOrganisationFinanceService;

    @Override
    protected OrganisationFinanceService getOrganisationFinanceService() {
        return projectOrganisationFinanceService;
    }
}
