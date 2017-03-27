package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.project.util.FinanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationFinanceDelegate {

    @Autowired
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandler;

    @Autowired
    private OrganisationJESFinance organisationJESFinance;

    @Autowired
    private FinanceUtil financeUtil;

    public OrganisationFinanceHandler getOrganisationFinanceHandler(Long organisationType) {
        if (financeUtil.isUsingJesFinances(organisationType)) {
            return organisationJESFinance;
        } else {
            return organisationFinanceDefaultHandler;
        }
    }
}
