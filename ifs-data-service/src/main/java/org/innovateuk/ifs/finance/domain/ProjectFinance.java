package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityStatus;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationSize;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * Entity object similar to ApplicationFinance for storing values in finance_row tables which can be edited by
 * internal project finance users.  It also holds organisation size because internal users will be allowed to edit
 * organisation size as well.
 */
@Entity
public class ProjectFinance extends Finance {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="projectId", referencedColumnName="id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="viabilityApprovalUserId", referencedColumnName="id")
    private User viabilityApprovalUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="eligibilityApprovalUserId", referencedColumnName="id")
    private User eligibilityApprovalUser;

    @Enumerated(EnumType.STRING)
    private Viability viability = Viability.REVIEW;

    @Enumerated(EnumType.STRING)
    private Eligibility eligibility = Eligibility.REVIEW;

    private boolean creditReportConfirmed = false;

    @Enumerated(EnumType.STRING)
    private ViabilityStatus viabilityStatus = ViabilityStatus.UNSET;

    @Enumerated(EnumType.STRING)
    private EligibilityStatus eligibilityStatus = EligibilityStatus.UNSET;

    private LocalDate viabilityApprovalDate;

    private LocalDate eligibilityApprovalDate;

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

    public User getViabilityApprovalUser() {
        return viabilityApprovalUser;
    }

    public void setViabilityApprovalUser(User viabilityApprovalUser) {
        this.viabilityApprovalUser = viabilityApprovalUser;
    }

    public Viability getViability() {
        return viability;
    }

    public void setViability(Viability viability) {
        this.viability = viability;
    }

    public boolean getCreditReportConfirmed() { return creditReportConfirmed; }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) { this.creditReportConfirmed = creditReportConfirmed; }

    public ViabilityStatus getViabilityStatus() {
        return viabilityStatus;
    }

    public void setViabilityStatus(ViabilityStatus viabilityStatus) {
        this.viabilityStatus = viabilityStatus;
    }

    public LocalDate getViabilityApprovalDate() {
        return viabilityApprovalDate;
    }

    public void setViabilityApprovalDate(LocalDate viabilityApprovalDate) {
        this.viabilityApprovalDate = viabilityApprovalDate;
    }

    public User getEligibilityApprovalUser() {
        return eligibilityApprovalUser;
    }

    public void setEligibilityApprovalUser(User eligibilityApprovalUser) {
        this.eligibilityApprovalUser = eligibilityApprovalUser;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }

    public void setEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public LocalDate getEligibilityApprovalDate() {
        return eligibilityApprovalDate;
    }

    public void setEligibilityApprovalDate(LocalDate eligibilityApprovalDate) {
        this.eligibilityApprovalDate = eligibilityApprovalDate;
    }
}
