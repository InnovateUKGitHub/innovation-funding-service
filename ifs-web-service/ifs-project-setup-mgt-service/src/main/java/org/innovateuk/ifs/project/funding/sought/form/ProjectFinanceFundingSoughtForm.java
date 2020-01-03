package org.innovateuk.ifs.project.funding.sought.form;

import javax.validation.Valid;
import java.util.Map;

public class ProjectFinanceFundingSoughtForm {

    @Valid
    private Map<Long, ProjectFinancePartnerFundingSoughtForm> partners;

    public Map<Long, ProjectFinancePartnerFundingSoughtForm> getPartners() {
        return partners;
    }

    public void setPartners(Map<Long, ProjectFinancePartnerFundingSoughtForm> partners) {
        this.partners = partners;
    }
}
