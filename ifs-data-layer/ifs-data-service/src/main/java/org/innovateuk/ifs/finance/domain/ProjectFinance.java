package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;

import javax.persistence.*;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

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

    private boolean creditReportConfirmed = false;

    @Enumerated(EnumType.STRING)
    private ViabilityRagStatus viabilityStatus = ViabilityRagStatus.UNSET;

    @Enumerated(EnumType.STRING)
    private EligibilityRagStatus eligibilityStatus = EligibilityRagStatus.UNSET;

    @OneToMany(mappedBy="projectFinance")
    private List<ProjectProcurementMilestone> milestones;

    public ProjectFinance() {
    }

    public ProjectFinance(Project project, Organisation organisation) {
        super(organisation);
        this.project = project;
    }

    public ProjectFinance(Organisation organisation, OrganisationSize organisationSize, Project project, GrowthTable growthTable, EmployeesAndTurnover employeesAndTurnover, KtpFinancialYears ktpFinancialYears, Boolean northernIrelandDeclaration, Boolean fecModelEnabled, FileEntry fecFileEntry) {
        super(organisation, organisationSize, growthTable, employeesAndTurnover, ktpFinancialYears, northernIrelandDeclaration, fecModelEnabled, fecFileEntry);
        this.project = project;
    }

    public ProjectFinance(Organisation organisation, OrganisationSize organisationSize, Project project) {
        this(organisation, organisationSize, project, null, null, null, null, null, null);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean getCreditReportConfirmed() { return creditReportConfirmed; }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) { this.creditReportConfirmed = creditReportConfirmed; }

    public ViabilityRagStatus getViabilityStatus() {
        return viabilityStatus;
    }

    public void setViabilityStatus(ViabilityRagStatus viabilityStatus) {
        this.viabilityStatus = viabilityStatus;
    }

    public EligibilityRagStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityRagStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public List<ProjectProcurementMilestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<ProjectProcurementMilestone> milestones) {
        this.milestones = milestones;
    }

    public boolean isPartner(Long userId) {
        return simpleAnyMatch(
                project.getProjectUsersWithRole(PROJECT_PARTNER),
                pu -> pu.isUser(userId) && pu.getOrganisation().equals(getOrganisation())
        );
    }

    public boolean isFinanceContact(Long userId) {
        return ofNullable(project.getExistingProjectUserWithRoleForOrganisation(PROJECT_FINANCE_CONTACT, getOrganisation()))
                .map(pu -> pu.isUser(userId)).orElse(false);
    }

    @Override
    public Application getApplication() {
        return getProject().getApplication();
    }
}