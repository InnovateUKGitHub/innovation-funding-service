package org.innovateuk.ifs.management.decision.populator;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionPaginationForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionSelectionForm;
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
public class CompetitionManagementFundingDecisionModelPopulator  {

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
                                                       FundingDecisionPaginationForm paginationForm,
                                                       FundingDecisionFilterForm fundingDecisionFilterForm,
                                                       FundingDecisionSelectionForm selectionForm,
                                                       UserResource user) {

        ApplicationSummaryPageResource results = getApplicationsByFilters(competitionId, paginationForm, fundingDecisionFilterForm);

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(competitionResource);

        List<Long> submittableApplicationIds = getAllApplicationIdsByFilters(competitionId, fundingDecisionFilterForm);
        boolean selectionLimitWarning = limitIsExceeded(submittableApplicationIds.size());
        boolean selectAllDisabled =  submittableApplicationIds.isEmpty();

        return new ManageFundingApplicationsViewModel(
                new Pagination(results),
                results,
                selectionForm,
                fundingDecisionFilterForm,
                competitionSummary,
                selectAllDisabled,
                selectionLimitWarning,
                !user.hasAuthority(Authority.COMP_ADMIN),
                fundingDecisionFilterForm.isEoi(),
                keyStatistics
        );
    }

    private ApplicationSummaryPageResource getApplicationsByFilters(long competitionId,
                                                                    FundingDecisionPaginationForm paginationForm,
                                                                    FundingDecisionFilterForm fundingDecisionFilterForm) {

        if (alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAssessedApplications(
                        competitionId,
                        "id",
                        paginationForm.getPage(),
                        PAGE_SIZE,
                        fundingDecisionFilterForm.getStringFilter(),
                        fundingDecisionFilterForm.getFundingFilter())
                        .getSuccess();
            }
        }
        return getSubmittedApplications(competitionId, paginationForm, fundingDecisionFilterForm);
    }

    private ApplicationSummaryPageResource getSubmittedApplications(long competitionId, FundingDecisionPaginationForm paginationForm, FundingDecisionFilterForm fundingDecisionFilterForm) {
        return fundingDecisionFilterForm.isEoi()
                ? applicationSummaryRestService.getSubmittedEoiApplications(competitionId, "id", paginationForm.getPage(),
                    PAGE_SIZE, fundingDecisionFilterForm.getStringFilter(), fundingDecisionFilterForm.getFundingFilter(), fundingDecisionFilterForm.getSendFilter()).getSuccess()
                : applicationSummaryRestService.getSubmittedApplications(competitionId, "id", paginationForm.getPage(),
                    PAGE_SIZE, fundingDecisionFilterForm.getStringFilter(), fundingDecisionFilterForm.getFundingFilter()).getSuccess();
    }

    private MultiValueMap<String, String> mapFormFilterParametersToMultiValueMap(FundingDecisionFilterForm fundingDecisionFilterForm) {
        MultiValueMap<String, String> filterMap = new LinkedMultiValueMap<>();
        if(fundingDecisionFilterForm.getFundingFilter().isPresent()) {
            filterMap.set("fundingFilter", fundingDecisionFilterForm.getFundingFilter().get().name());
        }
        if(fundingDecisionFilterForm.getStringFilter().isPresent()) {
            filterMap.set("stringFilter", fundingDecisionFilterForm.getStringFilter().get());
        }

        return filterMap;
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, FundingDecisionFilterForm filterForm) {
        if(alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAllAssessedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
            }
        }
        return getAllSubmittedApplicationIds(competitionId, filterForm);
    }

    public List<Long> getAllSubmittedApplicationIds(long competitionId, FundingDecisionFilterForm filterForm) {
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
