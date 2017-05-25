package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.resource.ApprovalType.APPROVED;
import static org.innovateuk.ifs.project.resource.ApprovalType.REJECTED;
import static org.innovateuk.ifs.project.sections.SectionStatus.*;

/**
 * This is a helper class for determining the status of a given Project Setup section
 */
public class ProjectSetupSectionStatus {

    public SectionStatus projectDetailsSectionStatus(final boolean projectDetailsProcessCompleted,
                                                     final boolean awaitingProjectDetailsActionFromPartners,
                                                     final boolean isLeadPartner) {
        if (projectDetailsProcessCompleted && !(isLeadPartner && awaitingProjectDetailsActionFromPartners)) {
            return TICK;
        } else {
            return FLAG;
        }
    }

    public SectionStatus monitoringOfficerSectionStatus(final boolean monitoringOfficerAssigned,
                                                        final boolean projectDetailsSubmitted) {
        if (monitoringOfficerAssigned) {
            return TICK;
        } else if (projectDetailsSubmitted) {
            return HOURGLASS;
        } else {
            return EMPTY;
        }

    }

    public SectionStatus bankDetailsSectionStatus(final ProjectActivityStates bankDetails) {
        if (bankDetails == null) {
            return EMPTY;
        } else if (PENDING.equals(bankDetails)) {
            return HOURGLASS;
        } else if (COMPLETE.equals(bankDetails)) {
            return TICK;
        } else {
            return FLAG;
        }
    }

    public SectionStatus financeChecksSectionStatus(final ProjectActivityStates financeCheckState,
                                                    final SectionAccess access) {

        if(access.equals(SectionAccess.NOT_ACCESSIBLE)) {
            return EMPTY;
        } else if (financeCheckState.equals(COMPLETE)) {
            return TICK;
        } else if (financeCheckState.equals(ACTION_REQUIRED)) {
            return FLAG;
        } else {
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
        } else {
            return EMPTY;
        }
    }

    public SectionStatus otherDocumentsSectionStatus(final ProjectResource project,
                                                     final boolean isProjectManager) {
        if (project.isPartnerDocumentsSubmitted() && APPROVED.equals(project.getOtherDocumentsApproved())) {
            return TICK;
        } else if (isProjectManager && (!project.isPartnerDocumentsSubmitted() || REJECTED.equals(project.getOtherDocumentsApproved()))) {
            return FLAG;
        } else {
            return HOURGLASS;
        }
    }

    public SectionStatus grantOfferLetterSectionStatus(final ProjectActivityStates grantOfferLetterState,
                                                       final boolean isLeadPartner) {
        if (grantOfferLetterState == null || NOT_REQUIRED.equals(grantOfferLetterState)) {
            return EMPTY;
        } else if (COMPLETE.equals(grantOfferLetterState)) {
            return TICK;
        } else if (isLeadPartner && ACTION_REQUIRED.equals(grantOfferLetterState)) {
            return FLAG;
        } else {
            return HOURGLASS;
        }
    }

}