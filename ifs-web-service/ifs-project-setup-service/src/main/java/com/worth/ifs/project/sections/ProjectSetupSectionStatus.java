package com.worth.ifs.project.sections;

import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.resource.ProjectResource;

import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.project.sections.SectionStatus.*;

import static java.util.Arrays.asList;

/**
 * This is a helper class for determining the status of a given Project Setup section
 */
public class ProjectSetupSectionStatus {

    public SectionStatus projectDetailsSectionStatus(final boolean projectDetailsProcessCompleted,
                                                     final boolean awaitingProjectDetailsActionFromPartners,
                                                     final boolean leadPartner) {
        if (!projectDetailsProcessCompleted) {
            if (leadPartner && awaitingProjectDetailsActionFromPartners) {
                return FLAG;
            }
            return FLAG;
        }
        return TICK;
    }

    public SectionStatus monitoringOfficerSectionStatus(final boolean monitoringOfficerAssigned,
                                                        final boolean projectDetailsSubmitted) {
        if (!monitoringOfficerAssigned) {
            if (projectDetailsSubmitted) {
                return HOURGLASS;
            }
            return EMPTY;
        }
        return TICK;
    }

    public SectionStatus bankDetailsSectionStatus(final ProjectActivityStates bankDetails) {
        if (bankDetails != null) {
            if (bankDetails.equals(ProjectActivityStates.PENDING)) {
                return HOURGLASS;
            } else if (bankDetails.equals(COMPLETE)) {
                return TICK;
            }
            return FLAG;
        }
        return EMPTY;
    }

    public SectionStatus financeChecksSectionStatus(final boolean allBankDetailsApprovedOrNotRequired,
                                                    final boolean allFinanceChecksApproved) {
        if (allBankDetailsApprovedOrNotRequired) {
            if (allFinanceChecksApproved) {
                return TICK;
            }
            return HOURGLASS;
        }
        return EMPTY;
    }

    public SectionStatus spendProfileSectionStatus(final ProjectActivityStates spendProfileState,
                                                   final boolean spendProfileApproved) {
        if (spendProfileState.equals(ProjectActivityStates.PENDING)) {
                return HOURGLASS;
            } else if (spendProfileState.equals(ProjectActivityStates.ACTION_REQUIRED)) {
                return FLAG;
            } else if (spendProfileState.equals(ProjectActivityStates.COMPLETE)) {
            if (spendProfileApproved) {
                return TICK;
            }
            return HOURGLASS;
        }
        return EMPTY;
    }

    public SectionStatus otherDocumentsSectionStatus(final ProjectResource project,
                                                     final boolean leadPartner) {
        if (project.isPartnerDocumentsSubmitted()) {
            if (project.getOtherDocumentsApproved() != null && project.getOtherDocumentsApproved()) {
                return TICK;
            }

            if (project.getOtherDocumentsApproved() != null && !project.getOtherDocumentsApproved() && leadPartner) {
                return FLAG;
            }
            return HOURGLASS;
        } else if (leadPartner) {
            return FLAG;
        }
        return HOURGLASS;
    }

    public SectionStatus grantOfferLetterSectionStatus(final ProjectActivityStates grantOfferLetterState,
                                                       final boolean leadPartner) {
        if(grantOfferLetterState != null) {
            if (COMPLETE.equals(grantOfferLetterState)) {
                return TICK;
            }
            if (leadPartner) {
                if (ACTION_REQUIRED.equals(grantOfferLetterState)) {
                    return FLAG;
                }
                if (asList(PENDING, NOT_STARTED).contains(grantOfferLetterState)) {
                    return HOURGLASS;
                }
                if (NOT_STARTED.equals(grantOfferLetterState)) {
                    return HOURGLASS;
                }
                return EMPTY;
            } else if (!NOT_REQUIRED.equals(grantOfferLetterState)) {
                return HOURGLASS;
            }
        }
        return EMPTY;
    }

}
