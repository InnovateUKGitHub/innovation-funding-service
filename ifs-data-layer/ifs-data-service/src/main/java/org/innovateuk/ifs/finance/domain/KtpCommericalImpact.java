package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class KtpCommericalImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int(11)")
    private Integer year;

    @Column(columnDefinition = "double")
    private BigDecimal inProjectProfit;

    @Column(columnDefinition = "TEXT")
    private String additionalIncomeStream;

    @Column(columnDefinition = "double")
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ktpCommericalImpactYearsId", referencedColumnName="id", nullable = false, updatable = false)
    private KtpCommercialImpactYears ktpCommercialImpactYears;

    KtpCommericalImpact() {}

    public KtpCommericalImpact(Integer year, BigDecimal inProjectProfit, String additionalIncomeStream, BigDecimal total, KtpCommercialImpactYears ktpCommercialImpactYears) {
        this.year = year;
        this.inProjectProfit = inProjectProfit;
        this.additionalIncomeStream = additionalIncomeStream;
        this.total = total;
        this.ktpCommercialImpactYears = ktpCommercialImpactYears;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public KtpCommercialImpactYears getKtpCommercialImpactYears() {
        return ktpCommercialImpactYears;
    }

    public void setKtpCommercialImpactYears(KtpCommercialImpactYears ktpCommercialImpactYears) {
        this.ktpCommercialImpactYears = ktpCommercialImpactYears;
    }
}
