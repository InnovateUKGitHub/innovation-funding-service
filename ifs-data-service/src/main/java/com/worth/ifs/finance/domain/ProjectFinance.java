package com.worth.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.resource.Viability;
import com.worth.ifs.project.finance.resource.ViabilityStatus;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationSize;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity object similar to ApplicationFinance for storing values in finance_row tables which can be edited by
 * internal project finance users.  It also holds organiation size because internal users will be allowed to edit
 * organisation size as well.
 */
@Entity
public class ProjectFinance extends Finance {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="projectId", referencedColumnName="id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private Viability viability = Viability.PENDING;

    private ViabilityStatus viabilityStatus;

    public ProjectFinance() {
    }

    public ProjectFinance(Organisation organisation, OrganisationSize organisationSize, Project project) {
        super(organisation, organisationSize);
        this.project = project;
    }

    @JsonIgnore
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Viability getViability() {
        return viability;
    }

    public void setViability(Viability viability) {
        this.viability = viability;
    }

    public ViabilityStatus getViabilityStatus() {
        return viabilityStatus;
    }

    public void setViabilityStatus(ViabilityStatus viabilityStatus) {
        this.viabilityStatus = viabilityStatus;
    }
}
