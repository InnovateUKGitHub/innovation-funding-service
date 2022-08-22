package org.innovateuk.ifs.management.decision.populator;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.form.DecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.DecisionPaginationForm;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionForm;
import org.innovateuk.ifs.management.decision.viewmodel.ManageFundingApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;

/**
 * Populator for the manage funding decisions view model
 */
@Component
public class CompetitionManagementApplicationDecisionModelPopulator {

    private static final int PAGE_SIZE = 20;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Value("${ifs.always.open.competition.enabled}")
    private boolean alwaysOpenCompetitionEnabled;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    public ManageFundingApplicationsViewModel populate(long competitionId,
                                                       DecisionPaginationForm paginationForm,
                                                       DecisionFilterForm decisionFilterForm,
                                                       DecisionSelectionForm selectionForm,
                                                       UserResource user) {

        ApplicationSummaryPageResource results = getApplicationsByFilters(competitionId, paginationForm, decisionFilterForm);

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(competitionResource);

        List<Long> submittableApplicationIds = getAllApplicationIdsByFilters(competitionId, decisionFilterForm);
        boolean selectionLimitWarning = limitIsExceeded(submittableApplicationIds.size());
        boolean selectAllDisabled =  submittableApplicationIds.isEmpty();

        return new ManageFundingApplicationsViewModel(
                new Pagination(results),
                results,
                selectionForm,
                decisionFilterForm,
                competitionSummary,
                selectAllDisabled,
                selectionLimitWarning,
                !user.hasAuthority(Authority.COMP_ADMIN),
                decisionFilterForm.isEoi(),
                keyStatistics
        );
    }

    private ApplicationSummaryPageResource getApplicationsByFilters(long competitionId,
                                                                    DecisionPaginationForm paginationForm,
                                                                    DecisionFilterForm decisionFilterForm) {

        if (alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAssessedApplications(
                        competitionId,
                        "id",
                        paginationForm.getPage(),
                        PAGE_SIZE,
                        decisionFilterForm.getStringFilter(),
                        decisionFilterForm.getFundingFilter())
                        .getSuccess();
            }
        }
        return getSubmittedApplications(competitionId, paginationForm, decisionFilterForm);
    }

    private ApplicationSummaryPageResource getSubmittedApplications(long competitionId, DecisionPaginationForm paginationForm, DecisionFilterForm decisionFilterForm) {
        return decisionFilterForm.isEoi()
                ? applicationSummaryRestService.getSubmittedEoiApplications(competitionId, "id", paginationForm.getPage(),
                    PAGE_SIZE, decisionFilterForm.getStringFilter(), decisionFilterForm.getFundingFilter(), decisionFilterForm.getSendFilter()).getSuccess()
                : applicationSummaryRestService.getSubmittedApplications(competitionId, "id", paginationForm.getPage(),
                    PAGE_SIZE, decisionFilterForm.getStringFilter(), decisionFilterForm.getFundingFilter()).getSuccess();
    }

    private MultiValueMap<String, String> mapFormFilterParametersToMultiValueMap(DecisionFilterForm decisionFilterForm) {
        MultiValueMap<String, String> filterMap = new LinkedMultiValueMap<>();
        if(decisionFilterForm.getFundingFilter().isPresent()) {
            filterMap.set("fundingFilter", decisionFilterForm.getFundingFilter().get().name());
        }
        if(decisionFilterForm.getStringFilter().isPresent()) {
            filterMap.set("stringFilter", decisionFilterForm.getStringFilter().get());
        }

        return filterMap;
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, DecisionFilterForm filterForm) {
        if(alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAllAssessedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
            }
        }
        return getAllSubmittedApplicationIds(competitionId, filterForm);
    }

    public List<Long> getAllSubmittedApplicationIds(long competitionId, DecisionFilterForm filterForm) {
        return filterForm.isEoi()
                ? applicationSummaryRestService.getAllSubmittedEoiApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter(), filterForm.getSendFilter()).getOrElse(emptyList())
                : applicationSummaryRestService.getAllSubmittedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
    }

    protected boolean limitIsExceeded(long amountOfIds) {
        return amountOfIds > SELECTION_LIMIT;
    }

    private CompetitionResource getCompetitionIfExist(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }
}
