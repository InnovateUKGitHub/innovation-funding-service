package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
public class KtpCommercialImpactYears extends FinancialYearAccounts {

    @JoinColumn(name = "application_id", referencedColumnName="id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @OneToMany(mappedBy="ktpCommercialImpactYears", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KtpCommercialImpact> years;

    @Column(columnDefinition = "double")
    private BigDecimal inProjectProfit;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalIncomeStream;

    public KtpCommercialImpactYears(Application application, List<KtpCommercialImpact> years, BigDecimal inProjectProfit, String additionalIncomeStream) {
        this.application = application;
        this.years = years;
        this.inProjectProfit = inProjectProfit;
        this.additionalIncomeStream = additionalIncomeStream;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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
