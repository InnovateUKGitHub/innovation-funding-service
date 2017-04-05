package org.innovateuk.ifs.project.sections;

import org.innovateuk.ifs.application.finance.model.UserRole;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.Map;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionInternalUser} to
 * determine which sections are available at a given time
 */
class ProjectSetupProgressChecker {

    private ProjectStatusResource projectStatus;

    public ProjectSetupProgressChecker(ProjectStatusResource projectStatusResource) {
        this.projectStatus = projectStatusResource;
    }

    public boolean isProjectDetailsSubmitted() {
        return COMPLETE.equals(projectStatus.getProjectDetailsStatus());
    }

    public boolean canAccessMonitoringOfficer() {
        return COMPLETE.equals(projectStatus.getMonitoringOfficerStatus())
                || ACTION_REQUIRED.equals(projectStatus.getMonitoringOfficerStatus());
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

    public boolean isOtherDocumentsSubmitted() {
        return ACTION_REQUIRED.equals(projectStatus.getOtherDocumentsStatus());
    }

    public boolean isOtherDocumentsApproved() {
        return COMPLETE.equals(projectStatus.getOtherDocumentsStatus());
    }

    public boolean isOtherDocumentsRejected() {
        return REJECTED.equals(projectStatus.getOtherDocumentsStatus());
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

    public Map<UserRoleType, ProjectActivityStates> getRoleSpecificActivityState() {
        return projectStatus.getRoleSpecificGrantOfferLetterState();
    }

    public boolean isGrantOfferLetterSent() {
        return projectStatus.getGrantOfferLetterSent();
    }
}
