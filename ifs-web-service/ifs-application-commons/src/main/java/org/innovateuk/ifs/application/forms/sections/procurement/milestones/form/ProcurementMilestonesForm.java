package org.innovateuk.ifs.application.forms.sections.procurement.milestones.form;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ProcurementMilestonesForm {
    public static final String UNSAVED_ROW_PREFIX = "unsaved-";

    public static String generateUnsavedRowId() {
        return UNSAVED_ROW_PREFIX + UUID.randomUUID().toString();
    }

    private Map<String, ProcurementMilestoneForm> milestones = new LinkedHashMap<>();

    public ProcurementMilestonesForm() {}

    public ProcurementMilestonesForm(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
    }

    public Map<String, ProcurementMilestoneForm> getMilestones() {
        return milestones;
    }

    public void setMilestones(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
    }

    public BigInteger getTotalPayments() {
        return milestones.values().stream()
                .map(ProcurementMilestoneForm::getPayment)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    public BigInteger getTotalPercentages(BigInteger fundingAmount) {
        return milestones.values().stream()
                .map(milestone -> milestone.getPercentageOfFundingAmount(fundingAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .toBigInteger();
    }
}
