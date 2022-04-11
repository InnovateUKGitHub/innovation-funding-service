package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.finance.resource.cost.OverheadRateType.*;

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

    private final boolean thirdPartyProcurementCompetition;

    private final boolean ktpCompetition;

    private final boolean ktpPhase2Enabled;

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

    private final String thirdPartyProjectCostGuidanceLink;

    private final boolean ofGemCompetition;

    private final boolean thirdPartyOfgem;

    private final boolean hecpCompetition;

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
                                     boolean thirdPartyProcurementCompetition,
                                     boolean ktpCompetition,
                                     boolean ktpPhase2Enabled,
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
                                     BigDecimal grantClaimPercentage,
                                     String thirdPartyProjectCostGuidanceLink,
                                     boolean ofGemCompetition,
                                     boolean thirdPartyOfgem,
                                     boolean hecpCompetition) {
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
        this.thirdPartyProcurementCompetition = thirdPartyProcurementCompetition;
        this.ktpCompetition = ktpCompetition;
        this.ktpPhase2Enabled = ktpPhase2Enabled;
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
        this.thirdPartyProjectCostGuidanceLink = thirdPartyProjectCostGuidanceLink;
        this.ofGemCompetition = ofGemCompetition;
        this.thirdPartyOfgem = thirdPartyOfgem;
        this.hecpCompetition = hecpCompetition;
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
                                     boolean thirdPartyProcurementCompetition,
                                     boolean ktpCompetition,
                                     boolean ktpPhase2Enabled,
                                     List<FinanceRowType> financeRowTypes,
                                     boolean overheadAlwaysTwenty,
                                     boolean showCovidGuidance,
                                     boolean showJustificationForm,
                                     Boolean fecModelEnabled,
                                     BigDecimal grantClaimPercentage,
                                     String thirdPartyProjectCostGuidanceLink,
                                     boolean ofGemCompetition,
                                     boolean thirdPartyOfgem,
                                     boolean hecpCompetition) {
        this(applicationId, competitionName, sectionId, competitionId, organisationId, complete, open,
                includeVat, applicationName, organisationName, financesUrl, procurementCompetition, thirdPartyProcurementCompetition,
                ktpCompetition, ktpPhase2Enabled, financeRowTypes, overheadAlwaysTwenty, showCovidGuidance, showJustificationForm, false,
                false, null, false, null, fecModelEnabled,
                grantClaimPercentage, thirdPartyProjectCostGuidanceLink, ofGemCompetition, thirdPartyOfgem, hecpCompetition);
    }

    public YourProjectCostsViewModel(boolean open, boolean internal, boolean procurementCompetition, boolean thirdPartyProcurementCompetition,
                                     boolean ktpCompetition, boolean ktpPhase2Enabled, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty,
                                     String competitionName, long applicationId, boolean thirdPartyOfgem, boolean hecpCompetition) {
        this.open = open;
        this.internal = internal;
        this.procurementCompetition = procurementCompetition;
        this.thirdPartyProcurementCompetition = thirdPartyProcurementCompetition;
        this.ktpCompetition = ktpCompetition;
        this.ktpPhase2Enabled = ktpPhase2Enabled;
        this.financeRowTypes = financeRowTypes;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.overheadAlwaysTwenty = overheadAlwaysTwenty;
        this.hecpCompetition = hecpCompetition;

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
        this.thirdPartyProjectCostGuidanceLink = null;
        this.ofGemCompetition = false;
        this.thirdPartyOfgem = thirdPartyOfgem;
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

    public boolean isThirdPartyProcurementCompetition() {
        return thirdPartyProcurementCompetition;
    }

    public boolean isKtpCompetition() { return ktpCompetition; }

    public boolean isKtpPhase2Enabled() { return ktpPhase2Enabled; }

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

    public String getThirdPartyProjectCostGuidanceLink() {
        return thirdPartyProjectCostGuidanceLink;
    }

    public Boolean isOfGemCompetition() {
        return ofGemCompetition;
    }

    public boolean isThirdPartyOfgem() {
        return thirdPartyOfgem;
    }


    public boolean isHecpCompetition() {
        return hecpCompetition;
    }

    @JsonIgnore
    public FinanceRowType getLabourFinanceRowType() {
        return FinanceRowType.LABOUR;
    }

    @JsonIgnore
    public FinanceRowType getOverheadsFinanceRowType() {
        return FinanceRowType.OVERHEADS;
    }

    @JsonIgnore
    public FinanceRowType getProcurementOverheadsFinanceRowType() {
        return FinanceRowType.PROCUREMENT_OVERHEADS;
    }

    @JsonIgnore
    public FinanceRowType getMaterialsFinanceRowType() {
        return FinanceRowType.MATERIALS;
    }

    @JsonIgnore
    public FinanceRowType getCapitalUsageFinanceRowType() {
        return FinanceRowType.CAPITAL_USAGE;
    }

    @JsonIgnore
    public FinanceRowType getSubContractingCostsFinanceRowType() {
        return FinanceRowType.SUBCONTRACTING_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getTravelFinanceRowType() {
        return FinanceRowType.TRAVEL;
    }

    @JsonIgnore
    public FinanceRowType getOtherCostsFinanceRowType() {
        return FinanceRowType.OTHER_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getVATFinanceRowType() {
        return FinanceRowType.VAT;
    }

    @JsonIgnore
    public FinanceRowType getAssociateSalaryCostsFinanceRowType() {
        return FinanceRowType.ASSOCIATE_SALARY_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getAssociateDevelopmentCostsFinanceRowType() {
        return FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS;
    }
    @JsonIgnore
    public FinanceRowType getEstateCostsFinanceRowType() {
        return FinanceRowType.ESTATE_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getKTPTravelFinanceRowType() {
        return FinanceRowType.KTP_TRAVEL;
    }

    @JsonIgnore
    public FinanceRowType getConsumabledFinanceRowType() {
        return FinanceRowType.CONSUMABLES;
    }

    @JsonIgnore
    public FinanceRowType getKnowledgeBaseFinanceRowType() {
        return FinanceRowType.KNOWLEDGE_BASE;
    }

    @JsonIgnore
    public FinanceRowType getAdditionalCompanyCostsFinanceRowType() {
        return FinanceRowType.ADDITIONAL_COMPANY_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getIndirectCostsFinanceRowType() {
        return FinanceRowType.INDIRECT_COSTS;
    }

    @JsonIgnore
    public FinanceRowType getAcademicAndSecretarialSupportFinanceRowType() {
        return FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT;
    }

    @JsonIgnore
    public FinanceRowType getAssociateSupportFinanceRowType() {
        return FinanceRowType.ASSOCIATE_SUPPORT;
    }
    @JsonIgnore
    public OverheadRateType getNoneOverheadRateType() {
        return NONE;
    }

    @JsonIgnore
    public OverheadRateType getDefaultPercentageOverheadRateType() {
        return DEFAULT_PERCENTAGE;
    }

    @JsonIgnore
    public OverheadRateType getTotalOverheadRateType() {
        return TOTAL;
    }

    @JsonIgnore
    public KtpTravelCost.KtpTravelCostType[] getAllKTPTravelCostTypes() {
        return KtpTravelCost.KtpTravelCostType.values();
    }
}