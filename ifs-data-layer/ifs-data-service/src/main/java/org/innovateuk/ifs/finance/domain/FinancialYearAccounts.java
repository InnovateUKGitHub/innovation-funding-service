package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;

@MappedSuperclass
public abstract class FinancialYearAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employees;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectFinanceId")
    private ProjectFinance projectFinance;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationFinanceId")
    private ApplicationFinance applicationFinance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployees() {
        return employees;
    }

    public void setEmployees(Long employees) {
        this.employees = employees;
    }

    public ProjectFinance getProjectFinance() {
        return projectFinance;
    }

    public void setProjectFinance(ProjectFinance projectFinance) {
        this.projectFinance = projectFinance;
    }

    public ApplicationFinance getApplicationFinance() {
        return applicationFinance;
    }

    public void setApplicationFinance(ApplicationFinance applicationFinance) {
        this.applicationFinance = applicationFinance;
    }
}
