package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.user.domain.Organisation;

import javax.persistence.*;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;

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

    public boolean isPartner(Long userId) {
        return ofNullable(project.getProjectUsersWithRole(PROJECT_PARTNER).stream().anyMatch(pu -> pu.isUser(userId) && pu.getOrganisation().equals(getOrganisation()))).orElse(false);
    }

    public boolean isFinanceContact(Long userId) {
        return ofNullable(project.getExistingProjectUserWithRoleForOrganisation(PROJECT_FINANCE_CONTACT, getOrganisation()))
                .map(pu -> pu.isUser(userId)).orElse(false);
    }
}
