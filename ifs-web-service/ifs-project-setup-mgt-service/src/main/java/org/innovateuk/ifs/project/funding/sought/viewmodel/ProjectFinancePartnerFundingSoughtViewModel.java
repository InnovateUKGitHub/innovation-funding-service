package org.innovateuk.ifs.project.funding.sought.viewmodel;

import java.math.BigDecimal;

public class ProjectFinancePartnerFundingSoughtViewModel {
    private final long id;
    private final String name;
    private final BigDecimal currentFunding;

    public ProjectFinancePartnerFundingSoughtViewModel(long id, String name, BigDecimal currentFunding) {
        this.id = id;
        this.name = name;
        this.currentFunding = currentFunding;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCurrentFunding() {
        return currentFunding;
    }
}
