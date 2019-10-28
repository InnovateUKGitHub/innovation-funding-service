package org.innovateuk.ifs.project.spendprofile.domain;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * Entity representing the Spend Profile of a Partner Organisation on a Project
 */
@Entity
public class SpendProfile {
    private static final String ELIGIBLE_COSTS_DESCRIPTION = "Eligible costs for Partner Organisation";
    private static final String SPEND_PROFILE_DESCRIPTION = "Spend Profile figures for Partner Organisation";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CostCategoryType costCategoryType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = ALL, optional = false)
    @JoinColumn(name = "eligible_costs_cost_group_id")
    private CostGroup eligibleCosts;

    @ManyToOne(fetch = FetchType.LAZY, cascade = ALL, optional = false)
    @JoinColumn(name = "spend_profile_figures_cost_group_id")
    private CostGroup spendProfileFigures;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User generatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar generatedDate;

    private boolean markedAsComplete;

    public SpendProfile() {
        // for ORM use
    }

    public SpendProfile(Organisation organisation, Project project, CostCategoryType costCategoryType, List<Cost> eligibleCosts, List<Cost> spendProfileFigures, User generatedBy, Calendar generatedDate, boolean markedAsComplete) {
        this.organisation = organisation;
        this.project = project;
        this.costCategoryType = costCategoryType;
        this.eligibleCosts = new CostGroup(ELIGIBLE_COSTS_DESCRIPTION, eligibleCosts);
        this.spendProfileFigures = new CostGroup(SPEND_PROFILE_DESCRIPTION, spendProfileFigures);
        this.generatedBy = generatedBy;
        this.generatedDate = generatedDate;
        this.markedAsComplete = markedAsComplete;
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

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public Calendar getGeneratedDate() {
        return generatedDate;
    }

}
