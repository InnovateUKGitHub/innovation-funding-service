package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class Labour {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private String projectRole;
    private Double grossAnnualSalary;
    private Integer labourDays;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public Labour(Long id, String projectRole, Double grossAnnualSalary, Integer labourDays) {
        this.id = id;
        this.projectRole = projectRole;
        this.grossAnnualSalary = grossAnnualSalary;
        this.labourDays = labourDays;
    }

    public Long getId() {
        return id;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public Double getGrossAnnualSalary() {
        return grossAnnualSalary;
    }

    public Integer getLabourDays() {
        return labourDays;
    }

}
