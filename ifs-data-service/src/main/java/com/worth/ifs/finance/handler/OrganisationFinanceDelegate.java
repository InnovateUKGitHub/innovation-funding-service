package com.worth.ifs.finance.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationFinanceDelegate {
    @Autowired
    OrganisationFinanceDefaultHandler organisationFinanceDefaultHandler;

    @Autowired
    OrganisationJESFinance organisationJESFinance;

    public OrganisationFinanceHandler getOrganisationFinanceHandler(String organisationType) {
        switch(organisationType) {
            case "Academic":
                return organisationJESFinance;
            default:
                return organisationFinanceDefaultHandler;
        }
    }
}
