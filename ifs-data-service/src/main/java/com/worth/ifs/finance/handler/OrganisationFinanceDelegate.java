package com.worth.ifs.finance.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationFinanceDelegate {
    @Autowired
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandler;

    @Autowired
    private OrganisationJESFinance organisationJESFinance;

    public OrganisationFinanceHandler getOrganisationFinanceHandler(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return organisationJESFinance;
            default:
                return organisationFinanceDefaultHandler;
        }
    }
}
