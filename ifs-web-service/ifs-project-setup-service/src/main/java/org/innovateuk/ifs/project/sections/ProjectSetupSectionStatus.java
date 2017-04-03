package org.innovateuk.ifs.project.sections;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.sections.SectionStatus.*;

import static java.util.Arrays.asList;

/**
 * This is a helper class for determining the status of a given Project Setup section
 */
public class ProjectSetupSectionStatus {

    public SectionStatus projectDetailsSectionStatus(final boolean projectDetailsProcessCompleted,
                                                     final boolean awaitingProjectDetailsActionFromPartners,
                                                     final boolean isLeadPartner) {
        if (!projectDetailsProcessCompleted) {
            if (isLeadPartner && awaitingProjectDetailsActionFromPartners) {
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
            if (PENDING.equals(bankDetails)) {
                return HOURGLASS;
            } else if (COMPLETE.equals(bankDetails)) {
                return TICK;
            }
            return FLAG;
        }
        return EMPTY;
    }

    public SectionStatus financeChecksSectionStatus(final ProjectActivityStates financeCheckState,
                                                    final SectionAccess access) {

        if(access.equals(SectionAccess.NOT_ACCESSIBLE)) {
            return EMPTY;
        } else {
            if (financeCheckState.equals(COMPLETE)) {
                return TICK;
            }
            if (financeCheckState.equals(ACTION_REQUIRED)) {
                return FLAG;
            }
            return HOURGLASS;
        }
    }

    public SectionStatus spendProfileSectionStatus(final ProjectActivityStates spendProfileState) {
        if (PENDING.equals(spendProfileState)) {
            return HOURGLASS;
        } else if (ACTION_REQUIRED.equals(spendProfileState)) {
            return FLAG;
        } else if (COMPLETE.equals(spendProfileState)) {
                return TICK;
        }
        return EMPTY;
    }

    public SectionStatus otherDocumentsSectionStatus(final ProjectResource project,
                                                     final boolean isProjectManager) {
        if (project.isPartnerDocumentsSubmitted()) {
            if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved())) {
                return TICK;
            }

            if (ApprovalType.REJECTED.equals(project.getOtherDocumentsApproved()) && isProjectManager) {
                return FLAG;
            }
            return HOURGLASS;
        } else if (isProjectManager) {
            return FLAG;
        }
        return HOURGLASS;
    }

    public SectionStatus grantOfferLetterSectionStatus(final ProjectActivityStates grantOfferLetterState,
                                                       final boolean isLeadPartner) {
        if(grantOfferLetterState != null) {
            if (COMPLETE.equals(grantOfferLetterState)) {
                return TICK;
            }
            if (isLeadPartner) {
                if (ACTION_REQUIRED.equals(grantOfferLetterState)) {
                    return FLAG;
                }
                if (asList(PENDING, NOT_STARTED).contains(grantOfferLetterState)) {
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
