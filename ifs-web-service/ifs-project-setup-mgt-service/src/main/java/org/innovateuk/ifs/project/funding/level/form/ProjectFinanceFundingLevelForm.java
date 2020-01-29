package org.innovateuk.ifs.project.funding.level.form;

import javax.validation.Valid;
import java.util.Map;

public class ProjectFinanceFundingLevelForm {

    @Valid
    private Map<Long, ProjectFinancePartnerFundingLevelForm> partners;

    public Map<Long, ProjectFinancePartnerFundingLevelForm> getPartners() {
        return partners;
    }

    public void setPartners(Map<Long, ProjectFinancePartnerFundingLevelForm> partners) {
        this.partners = partners;
    }
}
