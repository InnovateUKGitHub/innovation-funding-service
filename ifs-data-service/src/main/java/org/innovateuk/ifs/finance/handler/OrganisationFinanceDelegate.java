package org.innovateuk.ifs.finance.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationFinanceDelegate {

    public static final String UNIVERSITY_HEI = "University (HEI)";

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
            case UNIVERSITY_HEI:
                return true;
            default:
                return false;
        }
    }
}
