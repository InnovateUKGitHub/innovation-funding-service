package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.threads.resource.NoteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * View model backing the internal users' view of the Grant Offer Letter template page
 */
public class GrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String projectManagerFirstName;
    private final String projectManagerLastName;
    private final List<String> projectAddress;
    private final String competitionName;
    private final String projectName;
    private final String leadOrgName;
    private final List<NoteResource> notes;
    private final Map<String, String> termsAndConditionsTemplates;
    private final IndustrialFinanceTableModel industrialFinanceTable;
    private final AcademicFinanceTableModel academicFinanceTable;
    private final SummaryFinanceTableModel summaryFinanceTable;
    private final boolean subsidyControlGOLEnabled;
    private final SubsidyControlModel subsidyControlModel;
    private final boolean isProcurement;
    private final boolean thirdPartyOfgem;
    private final String leadOrganisationNumber;

    public GrantOfferLetterTemplateViewModel(long applicationId,
                                             String projectManagerFirstName,
                                             String projectManagerLastName,
                                             List<String> projectAddress,
                                             String competitionName,
                                             String projectName,
                                             String leadOrgName,
                                             String leadOrganisationNumber,
                                             List<NoteResource> notes,
                                             Map<String, String> termsAndConditionsTemplates,
                                             IndustrialFinanceTableModel industrialFinanceTable,
                                             AcademicFinanceTableModel academicFinanceTable,
                                             SummaryFinanceTableModel summaryFinanceTable,
                                             boolean subsidyControlGOLEnabled,
                                             SubsidyControlModel subsidyControlModel,
                                             boolean isProcurement,
                                             boolean thirdPartyOfgem) {
        this.applicationId = applicationId;
        this.projectManagerFirstName = projectManagerFirstName;
        this.projectManagerLastName = projectManagerLastName;
        this.projectAddress = projectAddress;
        this.competitionName = competitionName;
        this.projectName = projectName;
        this.leadOrgName = leadOrgName;
        this.leadOrganisationNumber = leadOrganisationNumber;
        this.notes = notes;
        this.termsAndConditionsTemplates = termsAndConditionsTemplates;
        this.industrialFinanceTable = industrialFinanceTable;
        this.academicFinanceTable = academicFinanceTable;
        this.summaryFinanceTable = summaryFinanceTable;
        this.subsidyControlGOLEnabled = subsidyControlGOLEnabled;
        this.subsidyControlModel = subsidyControlModel;
        this.isProcurement = isProcurement;
        this.thirdPartyOfgem = thirdPartyOfgem;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectManagerFirstName() {
        return projectManagerFirstName;
    }

    public String getProjectManagerLastName() {
        return projectManagerLastName;
    }

    public List<String> getProjectAddress() {
        return projectAddress;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLeadOrgName() {
        return leadOrgName;
    }

    public ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }

    public List<NoteResource> getNotes() {
        return notes;
    }

    public Map<String, String> getTermsAndConditionsTemplates() {
        return termsAndConditionsTemplates;
    }

    public boolean isSingleTermsAndConditionsTemplatePresent() {
        return termsAndConditionsTemplates.size() == 1;
    }

    public String getSingleTermsAndConditionsTemplate() {
        return termsAndConditionsTemplates.entrySet().stream().findAny().get().getValue();
    }

    public IndustrialFinanceTableModel getIndustrialFinanceTable() {
        return industrialFinanceTable;
    }

    public AcademicFinanceTableModel getAcademicFinanceTable() {
        return academicFinanceTable;
    }

    public SummaryFinanceTableModel getSummaryFinanceTable() {
        return summaryFinanceTable;
    }

    public boolean isSubsidyControlGOLEnabled() { return subsidyControlGOLEnabled; }

    public SubsidyControlModel getSubsidyControlModel() { return subsidyControlModel; }

    public boolean isProcurement() {
        return isProcurement;
    }

    public String getLeadOrganisationNumber() {
        return leadOrganisationNumber;
    }

    public boolean isThirdPartyOfgem() {
        return thirdPartyOfgem;
    }

    @JsonIgnore
    public FinanceRowType getLabourFinanceRowType() {
        return FinanceRowType.LABOUR;
    }
    @JsonIgnore
    public FinanceRowType getProcurementOverheadsFinanceRowType() {
        return FinanceRowType.PROCUREMENT_OVERHEADS;
    }

    @JsonIgnore
    public FinanceRowType getOverheadsFinanceRowType() {
        return FinanceRowType.OVERHEADS;
    }

    @JsonIgnore
    public FinanceRowType getHecpIndirectCostsFinanceRowType() {
        return FinanceRowType.HECP_INDIRECT_COSTS;
    }


    @JsonIgnore
    public FinanceRowType getMaterialsFinanceRowType() {
        return FinanceRowType.MATERIALS;
    }

    @JsonIgnore
    public FinanceRowType getEquipmentFinanceRowType() {
        return FinanceRowType.EQUIPMENT;
    }

    @JsonIgnore
    public FinanceRowType getCapitalUsageFinanceRowType() {
        return FinanceRowType.CAPITAL_USAGE;
    }

    @JsonIgnore
    public FinanceRowType getOtherGoodsFinanceRowType() {
        return FinanceRowType.OTHER_GOODS;
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
    public FinanceRowType getOtherFundingFinanceRowType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}

