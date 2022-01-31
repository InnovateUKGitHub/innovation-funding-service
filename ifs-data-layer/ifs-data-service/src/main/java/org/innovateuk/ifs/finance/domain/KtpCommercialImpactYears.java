package org.innovateuk.ifs.finance.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class KtpCommercialImpactYears<KtpCommercialImpact> extends FinancialYearAccounts {

    @OneToMany(mappedBy="ktpCommercialImpactYears", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KtpCommercialImpact> years;

    public KtpCommercialImpactYears(List<KtpCommercialImpact> years) {
        this.years = years;
    }

    public List<KtpCommercialImpact> getYears() {
        return years;
    }

    public void setYears(List<KtpCommercialImpact> years) {
        this.years = years;
    }
}
