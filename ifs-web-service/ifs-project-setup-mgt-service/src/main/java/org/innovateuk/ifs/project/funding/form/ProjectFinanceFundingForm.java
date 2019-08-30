package org.innovateuk.ifs.project.funding.form;

import java.util.Map;

public class ProjectFinanceFundingForm {

    private Map<Long, ProjectFinancePartnerFundingForm> partners;

    public Map<Long, ProjectFinancePartnerFundingForm> getPartners() {
        return partners;
    }

    public void setPartners(Map<Long, ProjectFinancePartnerFundingForm> partners) {
        this.partners = partners;
    }
}
