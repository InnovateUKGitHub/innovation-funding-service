package org.innovateuk.ifs.procurement.milestone.domain;


import org.innovateuk.ifs.finance.domain.ApplicationFinance;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ApplicationProcurementMilestone extends ProcurementMilestone {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public ApplicationFinance getApplicationFinance() {
        return applicationFinance;
    }

    public void setApplicationFinance(ApplicationFinance applicationFinance) {
        this.applicationFinance = applicationFinance;
    }
}
