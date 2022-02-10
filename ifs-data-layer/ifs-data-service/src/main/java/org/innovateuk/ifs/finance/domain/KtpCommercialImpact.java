package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class KtpCommercialImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int(11)")
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ktpCommercialImpactYearsId", referencedColumnName="id", nullable = false, updatable = false)
    private KtpCommercialImpactYears ktpCommercialImpactYears;

    public KtpCommercialImpact(Integer year, KtpCommercialImpactYears ktpCommercialImpactYears) {
        this.year = year;
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

    public KtpCommercialImpactYears getKtpCommercialImpactYears() {
        return ktpCommercialImpactYears;
    }

    public void setKtpCommercialImpactYears(KtpCommercialImpactYears ktpCommercialImpactYears) {
        this.ktpCommercialImpactYears = ktpCommercialImpactYears;
    }
}
