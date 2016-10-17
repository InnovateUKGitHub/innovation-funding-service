package com.worth.ifs.project.finance.domain;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;

@Entity
public class FinanceCheck {
    public static final String FINANCE_CHECK_COSTS_DESCRIPTION = "Finance check costs for partner for a project";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projectId", referencedColumnName = "id", nullable = false)
    private Project project;

    @OneToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "costGroupId", referencedColumnName = "id", nullable = false)
    private CostGroup costGroup;

    public FinanceCheck(Project project, CostGroup costGroup) {
        this.project = project;
        this.costGroup = costGroup;
    }

    public FinanceCheck() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CostGroup getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(CostGroup costGroup) {
        this.costGroup = costGroup;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}
