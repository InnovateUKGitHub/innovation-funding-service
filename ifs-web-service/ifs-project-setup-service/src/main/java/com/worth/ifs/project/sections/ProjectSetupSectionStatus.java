package com.worth.ifs.project.sections;

import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.resource.ProjectResource;

import static com.worth.ifs.project.sections.SectionStatus.*;

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
            } else if (bankDetails.equals(ProjectActivityStates.COMPLETE)) {
                return TICK;
            }
            return FLAG;
        }
        return EMPTY;
    }

    public SectionStatus financeChecksSectionStatus(final boolean allBankDetailsApprovedOrNotRequired,
                                                     final boolean allFinanceChecksApproved) {
        if(allBankDetailsApprovedOrNotRequired) {
           if(allFinanceChecksApproved) {
               return TICK;
           }
           return HOURGLASS;
        }
        return EMPTY;
    }

    public SectionStatus spendProfileSectionStatus(final boolean spendProfileSubmitted) {
        if(spendProfileSubmitted) {
            return TICK;
        }
        return EMPTY;
    }

    public SectionStatus otherDocumentsSectionStatus(final ProjectResource project,
                                                     final boolean leadPartner) {
        if (project.isPartnerDocumentsSubmitted()) {
            if (project.getOtherDocumentsApproved() != null) {
                return TICK;
            }
            return HOURGLASS;
        } else if (leadPartner) {
            return FLAG;
        }
        return HOURGLASS;
    }

    public SectionStatus grantOfferLetterSectionStatus(final boolean canAccessGrantOfferLetterSection,
                                                       final boolean leadPartner,
                                                       final boolean grantOfferLetterSubmitted) {
        if (canAccessGrantOfferLetterSection) {
            //TODO statuses for lead partners are not yet implemented
            if (leadPartner) {
                return FLAG;
            }
            return HOURGLASS;
        }
        return EMPTY;
    }

}
