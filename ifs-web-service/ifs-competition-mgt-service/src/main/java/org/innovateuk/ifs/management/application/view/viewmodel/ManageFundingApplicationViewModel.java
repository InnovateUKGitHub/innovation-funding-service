package org.innovateuk.ifs.management.application.view.viewmodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * View model for manage funding applications page
 */
public class ManageFundingApplicationViewModel {

    private ApplicationSummaryPageResource results;
    private String sortField;
    private long competitionId;
    private String competitionName;
    private CompetitionInFlightStatsViewModel keyStatistics;
    private Pagination pagination;
    private boolean selectAllDisabled;
    private boolean eoi;


    public ManageFundingApplicationViewModel(ApplicationSummaryPageResource results, CompetitionInFlightStatsViewModel keyStatistics, Pagination pagination, String sortField, long competitionId, String competitionName, boolean selectAllDisabled, boolean eoi) {
        this.results = results;
        this.sortField = sortField;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.keyStatistics = keyStatistics;
        this.pagination = pagination;
        this.selectAllDisabled = selectAllDisabled;
        this.eoi = eoi;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public ApplicationSummaryPageResource getResults() {
        return results;
    }

    public String getSortField() {
        return sortField;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationSummaryResource> getContent() {
        return results != null ? results.getContent() : emptyList();
    }

    public boolean isAnythingChangeable() {
        if (results != null) {
            return results.getContent().stream().anyMatch(ApplicationSummaryResource::applicationFundingDecisionIsChangeable);
        } else {
            return false;
        }
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }

    public boolean isEoi() {
        return eoi;
    }

    public void setEoi(boolean eoi) {
        this.eoi = eoi;
    }

    @JsonIgnore
    public FundingDecision getFundedFundingDecision() {
        return FundingDecision.FUNDED;
    }

    @JsonIgnore
    public FundingDecision getUnFundedFundingDecision() {
        return FundingDecision.UNFUNDED;
    }
    @JsonIgnore
    public FundingDecision getUnDecidedFundingDecision() {
        return FundingDecision.UNDECIDED;
    }
    @JsonIgnore
    public FundingDecision getOnHoldFundingDecision() {
        return FundingDecision.ON_HOLD;
    }
}
