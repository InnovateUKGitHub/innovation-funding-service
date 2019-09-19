package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public abstract class CompanyFinances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "application_finance_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ApplicationFinance applicationFinance;

    @JoinColumn(name = "project_finance_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ProjectFinance projectFinance;

    private Integer employees;

    public Long getId() {
        return id;
    }

    public ApplicationFinance getApplicationFinance() {
        return applicationFinance;
    }

    public void setApplicationFinance(ApplicationFinance applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public ProjectFinance getProjectFinance() {
        return projectFinance;
    }

    public void setProjectFinance(ProjectFinance projectFinance) {
        this.projectFinance = projectFinance;
    }

    public Integer getEmployees() {
        return employees;
    }

    public void setEmployees(Integer employees) {
        this.employees = employees;
    }

}
