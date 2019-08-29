package org.innovateuk.ifs.project.status.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.internal.ProjectSetupStages;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionInternalUser;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.project.status.viewmodel.InternalProjectSetupCell;
import org.innovateuk.ifs.project.status.viewmodel.InternalProjectSetupRow;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.project.internal.ProjectSetupStages.*;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * This class represents a populated CompetitionStatusViewModel.
 */
@Component
public class CompetitionStatusViewModelPopulator {

    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;
    private StatusRestService statusRestService;
    private CompetitionRestService competitionRestService;

    private CompetitionStatusViewModelPopulator() {
    }

    @Autowired
    public CompetitionStatusViewModelPopulator(CompetitionPostSubmissionRestService competitionPostSubmissionRestService,
                                               StatusRestService statusRestService,
                                               CompetitionRestService competitionRestService) {
        this.competitionPostSubmissionRestService = competitionPostSubmissionRestService;
        this.statusRestService = statusRestService;
        this.competitionRestService = competitionRestService;
    }

    public CompetitionStatusViewModel populate(UserResource user, Long competitionId, String applicationSearchString) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        final boolean hasProjectFinanceRole = user.hasRole(PROJECT_FINANCE);
        long openQueryCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess() : 0L;
        long pendingSpendProfilesCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId).getSuccess() : 0;
        CompetitionProjectsStatusResource competitionProjectsStatus = statusRestService.getCompetitionStatus(competitionId, StringUtils.trim(applicationSearchString)).getSuccess();

        List<InternalProjectSetupRow> internalProjectSetupRows = getProjectRows(competitionProjectsStatus, competition.getProjectSetupStages(), user);

        return new CompetitionStatusViewModel(
                competitionId,
                competition.getName(),
                hasProjectFinanceRole,
                openQueryCount,
                pendingSpendProfilesCount,
                applicationSearchString,
                internalProjectSetupRows);
    }

    private List<InternalProjectSetupRow> getProjectRows(CompetitionProjectsStatusResource competitionProjectsStatus, Set<ProjectSetupStages> columns, UserResource user) {
        return competitionProjectsStatus.getProjectStatusResources().stream()
                .map(status -> new InternalProjectSetupRow(
                        status.getProjectTitle(),
                        status.getApplicationNumber(),
                        status.getProjectState(),
                        status.getNumberOfPartners(),
                        competitionProjectsStatus.getCompetitionNumber(),
                        status.getProjectLeadOrganisationName(),
                        status.getProjectNumber(),
                        getProjectActivityStatesMap(status, columns, user, competitionProjectsStatus.getCompetitionNumber())
                )).collect(Collectors.toList());
    }

    private Map<ProjectSetupStages, InternalProjectSetupCell> getProjectActivityStatesMap(ProjectStatusResource status, Set<ProjectSetupStages> columns, UserResource user, long competitionId) {
        Map<ProjectSetupStages, InternalProjectSetupCell> activityStates = new LinkedHashMap<>();

        SetupSectionInternalUser setupSectionInternalUser = new SetupSectionInternalUser(status);

        if (columns.contains(PROJECT_DETAILS)) {
            activityStates.put(PROJECT_DETAILS,
                    new InternalProjectSetupCell(
                            status.getProjectDetailsStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/details"),
                            setupSectionInternalUser.canAccessProjectDetailsSection(user)
                    ));
        }
        if (columns.contains(PROJECT_TEAM)) {
            activityStates.put(PROJECT_TEAM,
                    new InternalProjectSetupCell(
                            status.getProjectTeamStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/team"),
                            setupSectionInternalUser.canAccessProjectDetailsSection(user)
                    ));
        }
        if (columns.contains(DOCUMENTS)) {
            activityStates.put(DOCUMENTS,
                    new InternalProjectSetupCell(
                            status.getDocumentsStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/document/all"),
                            setupSectionInternalUser.canAccessDocumentsSection(user)
                    ));
        }
        if (columns.contains(MONITORING_OFFICER)) {
            activityStates.put(MONITORING_OFFICER,
                    new InternalProjectSetupCell(
                            status.getMonitoringOfficerStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/monitoring-officer"),
                            setupSectionInternalUser.canAccessMonitoringOfficerSection(user)
                    ));
        }
        if (columns.contains(BANK_DETAILS)) {
            activityStates.put(BANK_DETAILS,
                    new InternalProjectSetupCell(
                            status.getBankDetailsStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/review-all-bank-details"),
                            setupSectionInternalUser.canAccessBankDetailsSection(user)
                    ));
        }
        if (columns.contains(FINANCE_CHECKS)) {
            activityStates.put(FINANCE_CHECKS,
                    new InternalProjectSetupCell(
                            status.getFinanceChecksStatus(),
                            String.format("/project-setup-management/project/" + status.getProjectNumber() + "/finance-check"),
                            setupSectionInternalUser.canAccessFinanceChecksSection(user)
                    ));
        }
        if (columns.contains(SPEND_PROFILE)) {
            activityStates.put(SPEND_PROFILE,
                    new InternalProjectSetupCell(
                            status.getSpendProfileStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/spend-profile/approval"),
                            setupSectionInternalUser.canAccessSpendProfileSection(user)
                    ));
        }
        if (columns.contains(GRANT_OFFER_LETTER)) {
            activityStates.put(GRANT_OFFER_LETTER,
                    new InternalProjectSetupCell(
                            status.getGrantOfferLetterStatus(),
                            String.format("/project-setup-management/competition/" + competitionId + "/project/" + status.getProjectNumber() + "/grant-offer-letter/send'"),
                            setupSectionInternalUser.canAccessGrantOfferLetterSection(user)
                    ));
        }

        return activityStates;
    }
}
