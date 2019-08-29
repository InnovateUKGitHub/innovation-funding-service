package org.innovateuk.ifs.project.status.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.project.status.viewmodel.InternalProjectSetupColumn;
import org.innovateuk.ifs.project.status.viewmodel.InternalProjectSetupRow;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.status.security.StatusHelper.projectStatusPermissions;
import static org.innovateuk.ifs.project.status.viewmodel.InternalProjectSetupColumn.*;
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

        List<InternalProjectSetupColumn> columns = asList(PROJECT_DETAILS, PROJECT_TEAM, DOCUMENTS, MONITORING_OFFICER, FINANCE_CHECKS, SPEND_PROFILE);
        List<InternalProjectSetupRow> internalProjectSetupRows = getProjectRows(competitionProjectsStatus, columns);

        return new CompetitionStatusViewModel(competitionProjectsStatus,
                hasProjectFinanceRole,
                projectStatusPermissions(user, competitionProjectsStatus),
                openQueryCount,
                pendingSpendProfilesCount,
                applicationSearchString,
                !competition.isLoan(),
//                asList(InternalProjectSetupColumn.values()),
                columns,
                internalProjectSetupRows);
    }

    private List<InternalProjectSetupRow> getProjectRows(CompetitionProjectsStatusResource competitionProjectsStatus, List<InternalProjectSetupColumn> columns) {
        return competitionProjectsStatus.getProjectStatusResources().stream()
                .map(status -> new InternalProjectSetupRow(
                        status.getProjectTitle(),
                        status.getApplicationNumber(),
                        status.getProjectState(),
                        status.getNumberOfPartners(),
                        competitionProjectsStatus.getCompetitionNumber(),
                        status.getProjectLeadOrganisationName(),
                        status.getProjectNumber(),
                        getProjectActivityStatesMap(status, columns)
                )).collect(Collectors.toList());
    }

    private Map<String, ProjectActivityStates> getProjectActivityStatesMap(ProjectStatusResource status, List<InternalProjectSetupColumn> columns) {
        Map<String, ProjectActivityStates> activityStates = new LinkedHashMap<>();

        if (columns.contains(PROJECT_DETAILS)) {
            activityStates.put("project-details", status.getProjectDetailsStatus());
        }
        if (columns.contains(PROJECT_TEAM)) {
            activityStates.put("project-team", status.getProjectTeamStatus());
        }
        if (columns.contains(DOCUMENTS)) {
            activityStates.put("documents", status.getDocumentsStatus());
        }
        if (columns.contains(MONITORING_OFFICER)) {
            activityStates.put("MO", status.getMonitoringOfficerStatus());
        }
        if (columns.contains(BANK_DETAILS)) {
            activityStates.put("bank-details", status.getBankDetailsStatus());
        }
        if (columns.contains(FINANCE_CHECKS)) {
            activityStates.put("finance-checks", status.getFinanceChecksStatus());
        }
        if (columns.contains(SPEND_PROFILE)) {
            activityStates.put("spend-profile", status.getSpendProfileStatus());
        }
        if (columns.contains(GRANT_OFFER_LETTER)) {
            activityStates.put("GOL", status.getGrantOfferLetterStatus());
        }

        return activityStates;
    }

}
