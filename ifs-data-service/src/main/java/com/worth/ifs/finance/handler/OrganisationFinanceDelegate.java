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
        if (isUsingJesFinances(organisationType)) {
            return organisationJESFinance;
        } else {
            return organisationFinanceDefaultHandler;
        }
    }

    public boolean isUsingJesFinances(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return true;
            default:
                return false;
        }
    }
}
