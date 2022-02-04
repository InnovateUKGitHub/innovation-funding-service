package org.innovateuk.ifs.finance.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
public class KtpCommercialImpactYears extends FinancialYearAccounts {

    @OneToMany(mappedBy="ktpCommercialImpactYears", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KtpCommercialImpact> years;

    @Column(columnDefinition = "double")
    private BigDecimal inProjectProfit;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalIncomeStream;

    public KtpCommercialImpactYears(List<KtpCommercialImpact> years, BigDecimal inProjectProfit, String additionalIncomeStream) {
        this.years = years;
        this.inProjectProfit = inProjectProfit;
        this.additionalIncomeStream = additionalIncomeStream;
    }

    public List<KtpCommercialImpact> getYears() {
        return years;
    }

    public void setYears(List<KtpCommercialImpact> years) {
        this.years = years;
    }

    public BigDecimal getInProjectProfit() {
        return inProjectProfit;
    }

    public void setInProjectProfit(BigDecimal inProjectProfit) {
        this.inProjectProfit = inProjectProfit;
    }

    public String getAdditionalIncomeStream() {
        return additionalIncomeStream;
    }

    public void setAdditionalIncomeStream(String additionalIncomeStream) {
        this.additionalIncomeStream = additionalIncomeStream;
    }
}
