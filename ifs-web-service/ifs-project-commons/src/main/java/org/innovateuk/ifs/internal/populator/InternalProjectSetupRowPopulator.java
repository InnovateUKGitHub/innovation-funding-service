package org.innovateuk.ifs.internal.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupCell;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionInternalUser;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
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
                        getProjectCells(status, competition.getProjectSetupStages(), user, competition.getId()),
                        ProjectActivityStates.COMPLETE == status.getGrantOfferLetterStatus(),
                        status.isSentToIfsPa()
                )).collect(Collectors.toList());
    }

    private Set<InternalProjectSetupCell> getProjectCells(ProjectStatusResource status, List<ProjectSetupStage> columns, UserResource user, long competitionId) {
        Set<InternalProjectSetupCell> cells = new LinkedHashSet<>();

        SetupSectionInternalUser setupSectionInternalUser = new SetupSectionInternalUser(status);

        if (isEmpty(columns)) {
            return cells;
        }

        return columns.stream().map(column -> {
            switch (column) {
                case PROJECT_DETAILS:
                    return new InternalProjectSetupCell(
                            status.getProjectDetailsStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/details"),
                            true,
                            PROJECT_DETAILS,
                            status.getProjectState()
                    );
                case PROJECT_TEAM:
                    return new InternalProjectSetupCell(
                            status.getProjectTeamStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/team"),
                            true,
                            PROJECT_TEAM,
                            status.getProjectState()
                    );
                case DOCUMENTS:
                    return new InternalProjectSetupCell(
                            status.getDocumentsStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/document/all"),
                            setupSectionInternalUser.canAccessDocumentsSection(user).isAccessible(),
                            DOCUMENTS,
                            status.getProjectState()
                    );
                case MONITORING_OFFICER:
                    return new InternalProjectSetupCell(
                            status.getMonitoringOfficerStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/monitoring-officer"),
                            setupSectionInternalUser.canAccessMonitoringOfficerSection(user).isAccessible(),
                            MONITORING_OFFICER,
                            status.getProjectState()
                    );
                case BANK_DETAILS:
                    return new InternalProjectSetupCell(
                            status.getBankDetailsStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/review-all-bank-details"),
                            setupSectionInternalUser.canAccessBankDetailsSection(user).isAccessible(),
                            BANK_DETAILS,
                            status.getProjectState()
                    );
                case FINANCE_CHECKS:
                    return new InternalProjectSetupCell(
                            status.getFinanceChecksStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/finance-check"),
                            setupSectionInternalUser.canAccessFinanceChecksSection(user).isAccessible(),
                            FINANCE_CHECKS,
                            status.getProjectState()
                    );
                case SPEND_PROFILE:
                    return new InternalProjectSetupCell(
                            status.getSpendProfileStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/spend-profile/approval"),
                            setupSectionInternalUser.canAccessSpendProfileSection(user).isAccessible(),
                            SPEND_PROFILE,
                            status.getProjectState()
                    );
                case GRANT_OFFER_LETTER:
                    return new InternalProjectSetupCell(
                            status.getGrantOfferLetterStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/grant-offer-letter/send"),
                            setupSectionInternalUser.canAccessGrantOfferLetterSendSection(user).isAccessible(),
                            GRANT_OFFER_LETTER,
                            status.getProjectState()
                    );
                case PROJECT_SETUP_COMPLETE:
                    return new InternalProjectSetupCell(
                            status.getProjectSetupCompleteStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/setup-complete"),
                            setupSectionInternalUser.canAccessProjectSetupComplete(user).isAccessible(),
                            PROJECT_SETUP_COMPLETE,
                            status.getProjectState()
                    );
                default:
                    return new InternalProjectSetupCell(
                            ProjectActivityStates.NOT_STARTED,
                            "",
                            false,
                            column,
                            status.getProjectState()
                    );
            }
        }).collect(toCollection(LinkedHashSet::new));
    }
}
