package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.Set;

public class YourProjectCostsViewModel implements BaseAnalyticsViewModel {
    private final Long applicationId;

    private final String competitionName;

    private final Long sectionId;

    private final Long organisationId;

    private final Long competitionId;

    private final boolean complete;

    private final boolean open;

    private final String applicationName;

    private final String organisationName;

    private final String financesUrl;

    private final boolean internal;

    private final boolean includeVat;

    private final boolean procurementCompetition;

    private final Set<FinanceRowType> financeRowTypes;

    public YourProjectCostsViewModel(long applicationId,
                                     String competitionName,
                                     long sectionId,
                                     long competitionId,
                                     long organisationId,
                                     boolean complete,
                                     boolean open,
                                     boolean includeVat,
                                     String applicationName,
                                     String organisationName,
                                     String financesUrl,
                                     boolean procurementCompetition,
                                     Set<FinanceRowType> financeRowTypes) {
        this.internal = false;
        this.organisationId = organisationId;
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.sectionId = sectionId;
        this.competitionId = competitionId;
        this.complete = complete;
        this.open = open;
        this.includeVat = includeVat;
        this.applicationName = applicationName;
        this.organisationName = organisationName;
        this.financesUrl = financesUrl;
        this.procurementCompetition = procurementCompetition;
        this.financeRowTypes = financeRowTypes;
    }

    public YourProjectCostsViewModel(boolean open, boolean internal, boolean procurementCompetition, Set<FinanceRowType> financeRowTypes, long competitionId, String competitionName) {
        this.open = open;
        this.internal = internal;
        this.procurementCompetition = procurementCompetition;
        this.financeRowTypes = financeRowTypes;
        this.competitionId = competitionId;
        this.competitionName = competitionName;

        this.applicationId = null;
        this.sectionId = null;
        this.organisationId = null;
        this.complete = false;
        this.applicationName = null;
        this.organisationName = null;
        this.financesUrl = null;
        this.includeVat = false;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isIncludeVat() {
        return includeVat;
    }

    public Set<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }

    public boolean isReadOnly(FinanceRowType type) {
        return isReadOnly();
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }
}