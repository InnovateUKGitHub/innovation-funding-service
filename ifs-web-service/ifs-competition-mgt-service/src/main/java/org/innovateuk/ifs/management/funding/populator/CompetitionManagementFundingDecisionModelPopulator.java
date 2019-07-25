package org.innovateuk.ifs.management.funding.populator;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.funding.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.funding.form.FundingDecisionPaginationForm;
import org.innovateuk.ifs.management.funding.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.management.funding.viewmodel.ManageFundingApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    public CompetitionManagementFundingDecisionModelPopulator(ApplicationSummaryRestService applicationSummaryRestService) {
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    public ManageFundingApplicationsViewModel populate(long competitionId,
                                                       FundingDecisionPaginationForm paginationForm,
                                                       FundingDecisionFilterForm fundingDecisionFilterForm,
                                                       FundingDecisionSelectionForm selectionForm) {

        ApplicationSummaryPageResource results = getApplicationsByFilters(competitionId, paginationForm, fundingDecisionFilterForm);

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccess();

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
                selectionLimitWarning
        );
    }

    private ApplicationSummaryPageResource getApplicationsByFilters(long competitionId,
                                                                    FundingDecisionPaginationForm paginationForm,
                                                                    FundingDecisionFilterForm fundingDecisionFilterForm) {
        return applicationSummaryRestService.getSubmittedApplications(
                competitionId,
                "id",
                paginationForm.getPage(),
                PAGE_SIZE,
                fundingDecisionFilterForm.getStringFilter(),
                fundingDecisionFilterForm.getFundingFilter())
                .getSuccess();
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
        return applicationSummaryRestService.getAllSubmittedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
    }

    protected boolean limitIsExceeded(long amountOfIds) {
        return amountOfIds > SELECTION_LIMIT;
    }
}
