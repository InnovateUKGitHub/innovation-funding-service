package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost.KtpTravelCostType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.IndirectCostsUtil.INDIRECT_COST_PERCENTAGE;

public class YourProjectCostsForm {

    public static final BigDecimal VAT_RATE = BigDecimal.valueOf(20);

    private LabourForm labour = new LabourForm();

    private PersonnelForm personnel = new PersonnelForm();

    private OverheadForm overhead = new OverheadForm();

    private HecpIndirectCostsForm hecpIndirectCosts = new HecpIndirectCostsForm();

    private Map<String, ProcurementOverheadRowForm> procurementOverheadRows = new LinkedHashMap<>();

    private Map<String, MaterialRowForm> materialRows = new LinkedHashMap<>();

    private Map<String, EquipmentRowForm> equipmentRows = new LinkedHashMap<>();

    private Map<String, CapitalUsageRowForm> capitalUsageRows = new LinkedHashMap<>();

    private Map<String, OtherGoodsRowForm> otherGoodsRows = new LinkedHashMap<>();

    private Map<String, SubcontractingRowForm> subcontractingRows = new LinkedHashMap<>();

    private Map<String, TravelRowForm> travelRows = new LinkedHashMap<>();

    private Map<String, OtherCostRowForm> otherRows = new LinkedHashMap<>();

    private VatForm vatForm;

    private Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows = new LinkedHashMap<>();

    private Map<String, AssociateDevelopmentCostRowForm> associateDevelopmentCostRows = new LinkedHashMap<>();

    private Map<String, ConsumablesRowForm> consumableCostRows = new LinkedHashMap<>();

    private Map<String, KnowledgeBaseCostRowForm> knowledgeBaseCostRows = new LinkedHashMap<>();

    private Map<String, AssociateSupportCostRowForm> associateSupportCostRows = new LinkedHashMap<>();

    private Map<String, EstateCostRowForm> estateCostRows = new LinkedHashMap<>();

    private Map<String, KtpTravelRowForm> ktpTravelCostRows = new LinkedHashMap<>();

    private AdditionalCompanyCostForm additionalCompanyCostForm = new AdditionalCompanyCostForm();

    private JustificationForm justificationForm = new JustificationForm();

    private AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm = new AcademicAndSecretarialSupportCostRowForm();

    private Boolean eligibleAgreement;

    private Boolean fecModelEnabled;

    private BigDecimal grantClaimPercentage;

    private boolean thirdPartyOfgem;

    public VatForm getVatForm() {
        return vatForm;
    }

    public void setVatForm(VatForm vatForm) {
        this.vatForm = vatForm;
    }

    public OverheadForm getOverhead() {
        return overhead;
    }

    public HecpIndirectCostsForm getHecpIndirectCosts() {
        return hecpIndirectCosts;
    }

    public void setOverhead(OverheadForm overhead) {
        this.overhead = overhead;
    }

    public void setHecpIndirectCosts(HecpIndirectCostsForm hecpIndirectCosts) {
        this.hecpIndirectCosts = hecpIndirectCosts;
    }

    public Map<String, ProcurementOverheadRowForm> getProcurementOverheadRows() {
        return procurementOverheadRows;
    }

    public void setProcurementOverheadRows(Map<String, ProcurementOverheadRowForm> procurementOverheadRows) {
        this.procurementOverheadRows = procurementOverheadRows;
    }

    public Map<String, MaterialRowForm> getMaterialRows() {
        return materialRows;
    }

    public Map<String, EquipmentRowForm> getEquipmentRows() {
        return equipmentRows;
    }

    public void setEquipmentRows(Map<String, EquipmentRowForm> equipmentRows) {
        this.equipmentRows = equipmentRows;
    }

    public void setMaterialRows(Map<String, MaterialRowForm> materialRows) {
        this.materialRows = materialRows;
    }

    public Map<String, CapitalUsageRowForm> getCapitalUsageRows() {
        return capitalUsageRows;
    }

    public void setCapitalUsageRows(Map<String, CapitalUsageRowForm> capitalUsageRows) {
        this.capitalUsageRows = capitalUsageRows;
    }

    public Map<String, OtherGoodsRowForm> getOtherGoodsRows() {
        return otherGoodsRows;
    }

    public void setOtherGoodsRows(Map<String, OtherGoodsRowForm> otherGoodsRows) {
        this.otherGoodsRows = otherGoodsRows;
    }

    public Map<String, SubcontractingRowForm> getSubcontractingRows() {
        return subcontractingRows;
    }

    public void setSubcontractingRows(Map<String, SubcontractingRowForm> subcontractingRows) {
        this.subcontractingRows = subcontractingRows;
    }

    public Map<String, TravelRowForm> getTravelRows() {
        return travelRows;
    }

    public void setTravelRows(Map<String, TravelRowForm> travelRows) {
        this.travelRows = travelRows;
    }

    public Map<String, OtherCostRowForm> getOtherRows() {
        return otherRows;
    }

    public void setOtherRows(Map<String, OtherCostRowForm> otherRows) {
        this.otherRows = otherRows;
    }

    public Boolean getEligibleAgreement() {
        return eligibleAgreement;
    }

    public void setEligibleAgreement(Boolean eligibleAgreement) {
        this.eligibleAgreement = eligibleAgreement;
    }

    public LabourForm getLabour() {
        return labour;
    }

    public void setLabour(LabourForm labour) {
        this.labour = labour;
    }

    public PersonnelForm getPersonnel() {
        return personnel;
    }

    public void setPersonnel(PersonnelForm personnel) {
        this.personnel = personnel;
    }

    public Map<String, AssociateSalaryCostRowForm> getAssociateSalaryCostRows() {
        return associateSalaryCostRows;
    }

    public void setAssociateSalaryCostRows(Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows) {
        this.associateSalaryCostRows = associateSalaryCostRows;
    }

    public Map<String, AssociateSupportCostRowForm> getAssociateSupportCostRows() {
        return associateSupportCostRows;
    }

    public void setAssociateSupportCostRows(Map<String, AssociateSupportCostRowForm> associateSupportCostRows) {
        this.associateSupportCostRows = associateSupportCostRows;
    }

    public Map<String, EstateCostRowForm> getEstateCostRows() {
        return estateCostRows;
    }

    public void setEstateCostRows(Map<String, EstateCostRowForm> estateCostRows) {
        this.estateCostRows = estateCostRows;
    }

    public AdditionalCompanyCostForm getAdditionalCompanyCostForm() {
        return additionalCompanyCostForm;
    }

    public void setAdditionalCompanyCostForm(AdditionalCompanyCostForm additionalCompanyCostForm) {
        this.additionalCompanyCostForm = additionalCompanyCostForm;
    }

    public Map<String, AssociateDevelopmentCostRowForm> getAssociateDevelopmentCostRows() {
        return associateDevelopmentCostRows;
    }

    public void setAssociateDevelopmentCostRows(Map<String, AssociateDevelopmentCostRowForm> associateDevelopmentCostRows) {
        this.associateDevelopmentCostRows = associateDevelopmentCostRows;
    }

    public Map<String, ConsumablesRowForm> getConsumableCostRows() {
        return consumableCostRows;
    }

    public void setConsumableCostRows(Map<String, ConsumablesRowForm> consumableCostRows) {
        this.consumableCostRows = consumableCostRows;
    }

    public Map<String, KnowledgeBaseCostRowForm> getKnowledgeBaseCostRows() {
        return knowledgeBaseCostRows;
    }

    public void setKnowledgeBaseCostRows(Map<String, KnowledgeBaseCostRowForm> knowledgeBaseCostRows) {
        this.knowledgeBaseCostRows = knowledgeBaseCostRows;
    }

    public Map<String, KtpTravelRowForm> getKtpTravelCostRows() {
        return ktpTravelCostRows;
    }

    public void setKtpTravelCostRows(Map<String, KtpTravelRowForm> ktpTravelCostRows) {
        this.ktpTravelCostRows = ktpTravelCostRows;
    }

    public JustificationForm getJustificationForm() {
        return justificationForm;
    }

    public void setJustificationForm(JustificationForm justificationForm) {
        this.justificationForm = justificationForm;
    }

    public Boolean getFecModelEnabled() {
        return fecModelEnabled;
    }

    public void setFecModelEnabled(Boolean fecModelEnabled) {
        this.fecModelEnabled = fecModelEnabled;
    }

    public BigDecimal getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(BigDecimal grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

    public boolean isThirdPartyOfgem() {
        return thirdPartyOfgem;
    }

    public void setThirdPartyOfgem(boolean thirdPartyOfgem) {
        this.thirdPartyOfgem = thirdPartyOfgem;
    }

    /* View methods. */
    public BigDecimal getVatTotal() {
        return getOrganisationFinanceTotal().multiply(VAT_RATE).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getProjectVatTotal() {
        return getOrganisationFinanceTotal().add(getVatTotal());
    }

    public BigDecimal getTotalLabourCosts() {
        return labour == null ? BigDecimal.ZERO : calculateTotal(labour.getRows());
    }

    public BigDecimal getTotalPersonnelCosts() {
        return personnel == null ? BigDecimal.ZERO : calculateTotal(personnel.getRows());
    }

    public BigDecimal getTotalAssociateSalaryCosts() {
        return calculateTotal(associateSalaryCostRows);
    }

    public BigDecimal getTotalGrantAssociateSalaryCosts() {
        return getTotalAssociateSalaryCosts()
                .multiply(grantClaimPercentage)
                .divide(new BigDecimal(100));
    }

    public BigDecimal getTotalOverheadCosts() {
        if (overhead != null && overhead.getRateType() != null) {
            switch (overhead.getRateType()) {
                case NONE:
                    return BigDecimal.ZERO;
                case DEFAULT_PERCENTAGE:
                    return getTotalLabourCosts().multiply(new BigDecimal("0.2"));
                case TOTAL:
                    return Optional.ofNullable(getOverhead().getTotalSpreadsheet()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                default:
                    return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalHecpIndirectCosts() {
        if (hecpIndirectCosts != null && hecpIndirectCosts.getRateType() != null) {
            switch (hecpIndirectCosts.getRateType()) {
                case NONE:
                    return BigDecimal.ZERO;
                case DEFAULT_PERCENTAGE:
                    return getTotalLabourCosts().multiply(new BigDecimal("0.2"));
                case HORIZON_EUROPE_GUARANTEE_TOTAL:
                    return Optional.ofNullable(getHecpIndirectCosts().getTotalSpreadsheet()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                default:
                    return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMaterialCosts() {
        return calculateTotal(materialRows);
    }

    public BigDecimal getTotalEquipmentCosts() {
        return calculateTotal(equipmentRows);
    }

    public BigDecimal getTotalProcurementOverheadCosts() {
        return calculateTotal(procurementOverheadRows);
    }

    public BigDecimal getTotalCapitalUsageCosts() {
        return calculateTotal(capitalUsageRows);
    }

    public BigDecimal getTotalOtherGoodsCosts() {
        return calculateTotal(otherGoodsRows);
    }

    public BigDecimal getTotalSubcontractingCosts() {
        return calculateTotal(subcontractingRows);
    }

    public BigDecimal getTotalTravelCosts() {
        return calculateTotal(travelRows);
    }

    public BigDecimal getTotalOtherCosts() {
        return calculateTotal(otherRows);
    }

    public BigDecimal getTotalAssociateSupportCosts() {
        return calculateTotal(associateSupportCostRows);
    }

    public BigDecimal getTotalAssociateDevelopmentCosts() {
        return calculateTotal(associateDevelopmentCostRows);
    }

    public BigDecimal getTotalConsumableCosts() {
        return calculateTotal(consumableCostRows);
    }

    public BigDecimal getTotalKnowledgeBaseCosts() {
        return calculateTotal(knowledgeBaseCostRows);
    }

    public BigDecimal getTotalEstateCosts() {
        return calculateTotal(estateCostRows);
    }

    public BigDecimal getTotalKtpTravelCosts() {
        return calculateTotal(ktpTravelCostRows);
    }

    public BigDecimal getTotalAcademicAndSecretarialSupportCosts() {
        return new BigDecimal(Optional.ofNullable(academicAndSecretarialSupportForm)
                .map(AcademicAndSecretarialSupportCostRowForm::getCost)
                .orElse(BigInteger.valueOf(0)));
    }

    public BigDecimal getTotalGrantAcademicAndSecretarialSupportCosts() {
        return getTotalAcademicAndSecretarialSupportCosts()
                .multiply(grantClaimPercentage)
                .divide(new BigDecimal(100));
    }

    public BigDecimal getIndirectCostsPercentage() {
        return INDIRECT_COST_PERCENTAGE;
    }

    public BigDecimal getTotalIndirectCosts()
    {
        if (BooleanUtils.isFalse(fecModelEnabled)) {
            return this.getTotalGrantAssociateSalaryCosts()
                    .add(this.getTotalGrantAcademicAndSecretarialSupportCosts())
                    .multiply(INDIRECT_COST_PERCENTAGE)
                    .divide(new BigDecimal(100))
                    .setScale(0, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getOrganisationFinanceTotal() {
        BigDecimal total = getTotalLabourCosts()
                .add(getTotalOverheadCosts())
                .add(getTotalHecpIndirectCosts())
                .add(getTotalMaterialCosts())
                .add(getTotalEquipmentCosts())
                .add(getTotalProcurementOverheadCosts())
                .add(getTotalCapitalUsageCosts())
                .add(getTotalOtherGoodsCosts())
                .add(getTotalSubcontractingCosts())
                .add(getTotalTravelCosts())
                .add(getTotalOtherCosts())
                .add(getTotalAssociateSalaryCosts())
                .add(getTotalAssociateDevelopmentCosts())
                .add(getTotalAssociateSupportCosts())
                .add(getTotalConsumableCosts())
                .add(getTotalKnowledgeBaseCosts())
                .add(getTotalEstateCosts())
                .add(getTotalKtpTravelCosts());

        if (BooleanUtils.isFalse(fecModelEnabled)) {
            return total.add(getTotalAcademicAndSecretarialSupportCosts())
                    .add(getTotalIndirectCosts());
        } else {
            return total;
        }
    }

    private BigDecimal calculateTotal(Map<String, ? extends AbstractCostRowForm> costRows) {
        return calculateTotal(costRows.values());
    }

    private BigDecimal calculateTotal(Collection<? extends AbstractCostRowForm> costRows) {
        return calculateTotal(costRows.stream());
    }

    private BigDecimal calculateTotal(Stream<? extends AbstractCostRowForm> costRows) {
        return costRows
                .map(AbstractCostRowForm::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(0, RoundingMode.HALF_UP);
    }

    public void recalculateTotals() {
        getLabour().getRows().forEach((id, row) -> {
            LabourCost cost = row.toCost(null);
            row.setTotal(cost.getTotal(getLabour().getWorkingDaysPerYear()));
            if (isThirdPartyOfgem()) {
                row.setRate(cost.getRate());
            } else {
                row.setRate(cost.getRate(getLabour().getWorkingDaysPerYear()));
           }
        });
        getOverhead().setTotal(getOverhead().getTotal());
        recalculateTotal(getMaterialRows());
        recalculateTotal(getEquipmentRows());
        recalculateTotal(getCapitalUsageRows());
        recalculateTotal(getOtherGoodsRows());
        recalculateTotal(getSubcontractingRows());
        recalculateTotal(getTravelRows());
        recalculateTotal(getOtherRows());

        recalculateTotal(getProcurementOverheadRows());

        recalculateTotal(getAssociateSalaryCostRows());
        recalculateTotal(getAssociateDevelopmentCostRows());
        recalculateTotal(getConsumableCostRows());
        recalculateTotal(getKnowledgeBaseCostRows());
        recalculateTotal(getEstateCostRows());
        recalculateTotal(getAssociateSupportCostRows());
        recalculateTotal(getKtpTravelCostRows());
    }

    public void orderAssociateCosts() {
        setAssociateSalaryCostRows(
                getAssociateSalaryCostRows().entrySet().stream()
                        .sorted(Comparator.comparing(entry -> entry.getValue().getRole()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
        );
        setAssociateDevelopmentCostRows(
                getAssociateDevelopmentCostRows().entrySet().stream()
                        .sorted(Comparator.comparing(entry -> entry.getValue().getRole()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
        );
    }

    private void recalculateTotal(Map<String, ? extends AbstractCostRowForm> rows) {
        rows.forEach((id, row) -> {
            FinanceRowItem cost = row.toCost(null);
            row.setTotal(cost.getTotal());
        });
    }

    public BigDecimal getTotalKtpTravelAssociateCosts() {
        return calculateTotal(ktpTravelCostRows
                .values()
                .stream()
                .filter(cost -> cost.getType() != null && cost.getType() == KtpTravelCostType.ASSOCIATE));
    }


    public BigDecimal getTotalKtpTravelSupervisorCosts() {
        return calculateTotal(ktpTravelCostRows
                .values()
                .stream()
                .filter(cost -> cost.getType() != null && cost.getType() == KtpTravelCostType.SUPERVISOR));
    }

    public AcademicAndSecretarialSupportCostRowForm getAcademicAndSecretarialSupportForm() {
        return academicAndSecretarialSupportForm;
    }

    public void setAcademicAndSecretarialSupportForm(AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm) {
        this.academicAndSecretarialSupportForm = academicAndSecretarialSupportForm;
    }
    @JsonIgnore
    public KtpTravelCost.KtpTravelCostType[] getAllKTPTravelCostTypes() {
        return KtpTravelCost.KtpTravelCostType.values();
    }
}
