package org.innovateuk.ifs.project.funding.form;

import javax.validation.Valid;
import java.util.Map;

public class ProjectFinanceFundingForm {

    @Valid
    private Map<Long, ProjectFinancePartnerFundingForm> partners;

    public Map<Long, ProjectFinancePartnerFundingForm> getPartners() {
        return partners;
    }

    public void setPartners(Map<Long, ProjectFinancePartnerFundingForm> partners) {
        this.partners = partners;
    }
}
