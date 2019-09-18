package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;

import java.util.Arrays;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionInternalUser} to
 * determine which sections are available at a given time
 */
class SetupProgressChecker {

    private ProjectStatusResource projectStatus;

    public SetupProgressChecker(ProjectStatusResource projectStatusResource) {
        this.projectStatus = projectStatusResource;
    }

    public boolean isProjectDetailsSubmitted() {
        return COMPLETE.equals(projectStatus.getProjectDetailsStatus());
    }

    public boolean canAccessMonitoringOfficer() {
        return Arrays.asList(COMPLETE, ACTION_REQUIRED).contains(projectStatus.getMonitoringOfficerStatus());
    }

    public boolean isBankDetailsApproved() {
        return COMPLETE.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isBankDetailsActionRequired() {
        return ACTION_REQUIRED.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isBankDetailsAccessible() {
        return !NOT_STARTED.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isBankDetailsQueried() {
        return PENDING.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isSpendProfileSubmitted() {
        return ACTION_REQUIRED.equals(projectStatus.getSpendProfileStatus());
    }

    public boolean isSpendProfileApproved() {
        return COMPLETE.equals(projectStatus.getSpendProfileStatus());
    }

    public boolean allDocumentsApproved() {
        return COMPLETE.equals(projectStatus.getDocumentsStatus());
    }

    public boolean isOrganisationRequiringFunding() {
        return !NOT_REQUIRED.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isMonitoringOfficerSubmitted() {
        return COMPLETE.equals(projectStatus.getMonitoringOfficerStatus());
    }

    public boolean isFinanceChecksSubmitted() {
        return COMPLETE.equals(projectStatus.getFinanceChecksStatus());
    }

    public ProjectActivityStates getGrantOfferLetterState() {
        return projectStatus.getGrantOfferLetterStatus();
    }

    public boolean isGrantOfferLetterSent() {
        return projectStatus.getGrantOfferLetterSent();
    }

    public boolean isGrantOfferLetterApproved() {
        return COMPLETE.equals(projectStatus.getGrantOfferLetterStatus());
    }

    public boolean isOffline() {
        return projectStatus.getProjectState().isOffline();
    }
}
