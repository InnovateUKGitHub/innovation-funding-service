package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.sections.SectionStatus.*;

/**
 * This is a helper class for determining the status of a given Project Setup section
 */
@Component
public class SetupSectionStatus {

    public SectionStatus projectDetailsSectionStatus(final boolean projectDetailsProcessCompleted,
                                                     final boolean awaitingProjectDetailsActionFromPartners,
                                                     final boolean isLeadPartner) {
        if (projectDetailsProcessCompleted && !(isLeadPartner && awaitingProjectDetailsActionFromPartners)) {
            return TICK;
        } else {
            return FLAG;
        }
    }

    public SectionStatus projectTeamSectionStatus(final ProjectActivityStates projectTeamStatus) {
        return COMPLETE.equals(projectTeamStatus) ?
                TICK : FLAG;
    }

    public SectionStatus monitoringOfficerSectionStatus(final boolean monitoringOfficerAssigned,
                                                        final boolean requiredProjectDetailsForMonitoringOfficerComplete) {
        if (monitoringOfficerAssigned) {
            return TICK;
        } else {
            return HOURGLASS;
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

        if (access.equals(SectionAccess.NOT_ACCESSIBLE)) {
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

    public SectionStatus projectSetupCompleteStatus(final ProjectActivityStates setupSectionState) {
        if (setupSectionState.equals(COMPLETE)) {
            return TICK;
        } else if (setupSectionState.equals(PENDING)) {
            return HOURGLASS;
        } else {
            return EMPTY;
        }
    }

    public SectionStatus documentsSectionStatus(final boolean isProjectManager,
                                                ProjectResource project,
                                                CompetitionResource competition) {
        List<CompetitionDocumentResource> competitionDocuments = competition.getCompetitionDocuments();
        List<ProjectDocumentResource> projectDocuments = project.getProjectDocuments();

        if (!project.isCollaborativeProject()) {
            competitionDocuments.removeIf(
                    document -> document.getTitle().equals(COLLABORATION_AGREEMENT_TITLE));
            projectDocuments.removeIf(
                    document -> document.getCompetitionDocument().getTitle().equals(COLLABORATION_AGREEMENT_TITLE));
        }

        int actualNumberOfDocuments = projectDocuments.size();
        int expectedNumberOfDocuments = competitionDocuments.size();

        if (actualNumberOfDocuments == expectedNumberOfDocuments && projectDocuments.stream()
                .allMatch(projectDocumentResource -> DocumentStatus.APPROVED.equals(projectDocumentResource.getStatus()))) {
            return TICK;
        }

        if (actualNumberOfDocuments != expectedNumberOfDocuments || projectDocuments.stream()
                .anyMatch(projectDocumentResource -> DocumentStatus.UPLOADED.equals(projectDocumentResource.getStatus())
                        || DocumentStatus.REJECTED.equals(projectDocumentResource.getStatus())
                        || DocumentStatus.REJECTED_DUE_TO_TEAM_CHANGE.equals(projectDocumentResource.getStatus()))) {
            return isProjectManager ? FLAG : EMPTY;
        }

        return HOURGLASS;
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