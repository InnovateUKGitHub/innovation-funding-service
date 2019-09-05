package org.innovateuk.ifs.internal.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupCell;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionInternalUser;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

@Component
public class InternalProjectSetupRowPopulator {

    public List<InternalProjectSetupRow> populate(List<ProjectStatusResource> projectStatusResources, CompetitionResource competition, UserResource user) {

        return projectStatusResources.stream()
                .map(status -> new InternalProjectSetupRow(
                        status.getProjectTitle(),
                        status.getApplicationNumber(),
                        status.getProjectState(),
                        status.getNumberOfPartners(),
                        competition.getId(),
                        status.getProjectLeadOrganisationName(),
                        status.getProjectNumber(),
                        getProjectCells(status, competition.getProjectSetupStages(), user, competition.getId())
                )).collect(Collectors.toList());
    }

    private Set<InternalProjectSetupCell> getProjectCells(ProjectStatusResource status, List<ProjectSetupStage> columns, UserResource user, long competitionId) {
        Set<InternalProjectSetupCell> cells = new LinkedHashSet<>();

        SetupSectionInternalUser setupSectionInternalUser = new SetupSectionInternalUser(status);

        if (isEmpty(columns)) {
            return cells;
        }

        if (columns.contains(PROJECT_DETAILS)) {
            cells.add(new InternalProjectSetupCell(
                    PROJECT_DETAILS,
                    status.getProjectDetailsStatus(),
                    String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/details"),
                    setupSectionInternalUser.canAccessProjectDetailsSection(user)
            ));
        }
        if (columns.contains(PROJECT_TEAM)) {
            cells.add(new InternalProjectSetupCell(
                    PROJECT_TEAM,
                    status.getProjectTeamStatus(),
                    String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/team"),
                    setupSectionInternalUser.canAccessProjectDetailsSection(user)
            ));
        }
        if (columns.contains(DOCUMENTS)) {
            cells.add(new InternalProjectSetupCell(
                    DOCUMENTS,
                    status.getDocumentsStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/document/all"),
                    setupSectionInternalUser.canAccessDocumentsSection(user)
            ));
        }
        if (columns.contains(MONITORING_OFFICER)) {
            cells.add(new InternalProjectSetupCell(
                    MONITORING_OFFICER,
                    status.getMonitoringOfficerStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/monitoring-officer"),
                    setupSectionInternalUser.canAccessMonitoringOfficerSection(user)
            ));
        }
        if (columns.contains(BANK_DETAILS)) {
            cells.add(new InternalProjectSetupCell(
                    BANK_DETAILS,
                    status.getBankDetailsStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/review-all-bank-details"),
                    setupSectionInternalUser.canAccessBankDetailsSection(user)
            ));
        }
        if (columns.contains(FINANCE_CHECKS)) {
            cells.add(new InternalProjectSetupCell(
                    FINANCE_CHECKS,
                    status.getFinanceChecksStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/finance-check"),
                    setupSectionInternalUser.canAccessFinanceChecksSection(user)
            ));
        }
        if (columns.contains(SPEND_PROFILE)) {
            cells.add(new InternalProjectSetupCell(
                    SPEND_PROFILE,
                    status.getSpendProfileStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/spend-profile/approval"),
                    setupSectionInternalUser.canAccessSpendProfileSection(user)
            ));
        }
        if (columns.contains(GRANT_OFFER_LETTER)) {
            cells.add(new InternalProjectSetupCell(
                    GRANT_OFFER_LETTER,
                    status.getGrantOfferLetterStatus(),
                    String.format("/project-setup-management/project/" + status.getProjectNumber() + "/grant-offer-letter/send"),
                    setupSectionInternalUser.canAccessGrantOfferLetterSendSection(user)
            ));
        }

        return cells;
    }
}
