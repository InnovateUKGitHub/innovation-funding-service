package com.worth.ifs.project.sections;

import com.worth.ifs.project.status.resource.ProjectStatusResource;

import static com.worth.ifs.project.constant.ProjectActivityStates.*;

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

    public boolean isBankDetailsApproved() {
        return COMPLETE.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isBankDetailsActionRequired() {
        return ACTION_REQUIRED.equals(projectStatus.getBankDetailsStatus());
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

    public boolean isOrganisationRequiringFunding() {
        return !NOT_REQUIRED.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isMonitoringOfficerSubmitted() {
        return COMPLETE.equals(projectStatus.getMonitoringOfficerStatus());
    }

    public boolean isFinanceChecksSubmitted() {
        return COMPLETE.equals(projectStatus.getFinanceChecksStatus());
    }

    public boolean isGrantOfferLetterSent() {
        return COMPLETE.equals(projectStatus.getGrantOfferLetterStatus());
        //return SENT.equals(projectStatus.getGrantOfferLetterStatus());
    }

    public boolean isGrantOfferLetterReadyToApprove() {
        return COMPLETE.equals(projectStatus.getGrantOfferLetterStatus());
        //return READY_TO_APPROVE.equals(projectStatus.getGrantOfferLetterStatus());
    }

    public boolean isGrantOfferLetterApproved() {
        return COMPLETE.equals(projectStatus.getGrantOfferLetterStatus());
        //return APPROVED.equals(projectStatus.getGrantOfferLetterStatus());
    }
}
