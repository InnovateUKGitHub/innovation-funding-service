package com.worth.ifs.project.finance.domain;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;

/**
 * Entity representing the Spend Profile of a Partner Organisation on a Project
 */
@Entity
public class SpendProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Organisation organisation;

    @ManyToOne(optional = false)
    private Project project;

    @ManyToOne(optional = false)
    private CostCategoryType costCategoryType;

    @ManyToOne(cascade = ALL, optional = false)
    @JoinColumn(name = "eligible_costs_cost_group_id")
    private CostGroup eligibleCosts;

    @ManyToOne(cascade = ALL, optional = false)
    @JoinColumn(name = "spend_profile_figures_cost_group_id")
    private CostGroup spendProfileFigures;

    SpendProfile() {
        // for ORM use
    }

    public SpendProfile(Organisation organisation, Project project, CostCategoryType costCategoryType, CostGroup eligibleCosts, CostGroup spendProfileFigures) {
        this.organisation = organisation;
        this.project = project;
        this.costCategoryType = costCategoryType;
        this.eligibleCosts = eligibleCosts;
        this.spendProfileFigures = spendProfileFigures;
    }

    public Long getId() {
        return id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Project getProject() {
        return project;
    }

    public CostCategoryType getCostCategoryType() {
        return costCategoryType;
    }

    public CostGroup getEligibleCosts() {
        return eligibleCosts;
    }

    public CostGroup getSpendProfileFigures() {
        return spendProfileFigures;
    }
}
