package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    private final boolean ktpCompetition;

    private final List<FinanceRowType> financeRowTypes;

    private final boolean overheadAlwaysTwenty;

    private final boolean showCovidGuidance;

    private final boolean showJustificationForm;

    private final boolean projectCostSectionLocked;

    private final boolean yourFundingRequired;

    private final Long yourFundingSectionId;

    private final boolean yourFecCostRequired;

    private final Long yourFecCostSectionId;

    private final Boolean fecModelEnabled;

    private final BigDecimal grantClaimPercentage;

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
                                     boolean ktpCompetition,
                                     List<FinanceRowType> financeRowTypes,
                                     boolean overheadAlwaysTwenty,
                                     boolean showCovidGuidance,
                                     boolean showJustificationForm,
                                     boolean projectCostSectionLocked,
                                     boolean yourFundingRequired,
                                     Long yourFundingSectionId,
                                     boolean yourFecCostRequired,
                                     Long yourFecCostSectionId,
                                     Boolean fecModelEnabled,
                                     BigDecimal grantClaimPercentage) {
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
        this.ktpCompetition = ktpCompetition;
        this.financeRowTypes = financeRowTypes;
        this.overheadAlwaysTwenty = overheadAlwaysTwenty;
        this.showCovidGuidance = showCovidGuidance;
        this.showJustificationForm = showJustificationForm;
        this.projectCostSectionLocked = projectCostSectionLocked;
        this.yourFundingRequired = yourFundingRequired;
        this.yourFundingSectionId = yourFundingSectionId;
        this.yourFecCostRequired = yourFecCostRequired;
        this.yourFecCostSectionId = yourFecCostSectionId;
        this.fecModelEnabled = fecModelEnabled;
        this.grantClaimPercentage = grantClaimPercentage;
    }

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
                                     boolean ktpCompetition,
                                     List<FinanceRowType> financeRowTypes,
                                     boolean overheadAlwaysTwenty,
                                     boolean showCovidGuidance,
                                     boolean showJustificationForm,
                                     Boolean fecModelEnabled,
                                     BigDecimal grantClaimPercentage) {
        this(applicationId, competitionName, sectionId, competitionId, organisationId, complete, open,
                includeVat, applicationName, organisationName, financesUrl, procurementCompetition, ktpCompetition, financeRowTypes,
                overheadAlwaysTwenty, showCovidGuidance, showJustificationForm, false, false, null, false, null, fecModelEnabled, grantClaimPercentage);
    }

    public YourProjectCostsViewModel(boolean open, boolean internal, boolean procurementCompetition, boolean ktpCompetition, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, String competitionName, long applicationId) {
        this.open = open;
        this.internal = internal;
        this.procurementCompetition = procurementCompetition;
        this.ktpCompetition = ktpCompetition;
        this.financeRowTypes = financeRowTypes;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.overheadAlwaysTwenty = overheadAlwaysTwenty;

        this.competitionId = null;
        this.sectionId = null;
        this.organisationId = null;
        this.complete = false;
        this.applicationName = null;
        this.organisationName = null;
        this.financesUrl = null;
        this.includeVat = false;
        this.showCovidGuidance = false;
        this.showJustificationForm = false;
        this.projectCostSectionLocked = false;
        this.yourFundingRequired = false;
        this.yourFundingSectionId = null;
        this.yourFecCostRequired = false;
        this.yourFecCostSectionId = null;
        this.fecModelEnabled = null;
        this.grantClaimPercentage = BigDecimal.ZERO;
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

    public List<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }

    public boolean isOverheadAlwaysTwenty() {
        return overheadAlwaysTwenty;
    }

    public boolean isShowCovidGuidance() {
        return showCovidGuidance;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isShowJustificationForm() {
        return showJustificationForm;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }

    public boolean showEditButton(FinanceRowType type) {
        return !type.equals(FinanceRowType.INDIRECT_COSTS);
    }

    public List<FinanceRowType> getOrderedAccordionFinanceRowTypes() {
        return financeRowTypes.stream().filter(FinanceRowType::isAppearsInProjectCostsAccordion).collect(Collectors.toList());
    }

    public String getStateAidCheckboxLabelFragment() {
        return isKtpCompetition() ? "ktp_state_aid_checkbox_label" : "state_aid_checkbox_label";
    }

    public boolean isProjectCostSectionLocked() {
        return projectCostSectionLocked;
    }

    public boolean isYourFundingRequired() {
        return yourFundingRequired;
    }

    public Long getYourFundingSectionId() {
        return yourFundingSectionId;
    }

    public boolean isYourFecCostRequired() {
        return yourFecCostRequired;
    }

    public Long getYourFecCostSectionId() {
        return yourFecCostSectionId;
    }

    public Boolean getFecModelEnabled() {
        return fecModelEnabled;
    }

    public boolean isFecModelDisabled() {
        return BooleanUtils.isFalse(fecModelEnabled);
    }

    public BigDecimal getGrantClaimPercentage() {
        return grantClaimPercentage;
    }
}