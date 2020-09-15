package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
public class KtpFinancialYears extends FinancialYearAccounts {

    @OneToMany(mappedBy="ktpFinancialYears", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KtpFinancialYear> years;

    private Long groupEmployees;

    private LocalDate financialYearEnd;

    public KtpFinancialYears() {}

    public KtpFinancialYears(KtpFinancialYears ktpFinancialYears) {
        years = ktpFinancialYears.getYears().stream()
                .map((year) -> new KtpFinancialYear(year, this))
                .collect(toList());
        groupEmployees = ktpFinancialYears.getGroupEmployees();
        financialYearEnd = ktpFinancialYears.getFinancialYearEnd();
    }
    public List<KtpFinancialYear> getYears() {
        return years;
    }

    public void setYears(List<KtpFinancialYear> years) {
        this.years = years;
    }

    public Long getGroupEmployees() {
        return groupEmployees;
    }

    public void setGroupEmployees(Long groupEmployees) {
        this.groupEmployees = groupEmployees;
    }

    public LocalDate getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(LocalDate financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }
}
