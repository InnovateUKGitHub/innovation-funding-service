package org.innovateuk.ifs.application.forms.sections.procurement.milestones.form;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class ProcurementMilestonesForm {

    private Map<String, ProcurementMilestoneForm> milestones;
    private BigInteger totalCosts;

    public ProcurementMilestonesForm() {}

    public ProcurementMilestonesForm(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
        calculateTotalCosts();
    }


    public Map<String, ProcurementMilestoneForm> getMilestones() {
        return milestones;
    }

    public void setMilestones(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
        calculateTotalCosts();
    }

    public BigInteger getTotalCosts() {
        return totalCosts;
    }

    public BigInteger getTotalPercentages() {
        return milestones.values().stream()
                .map(milestone -> milestone.getPercentageOfCost(totalCosts))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .toBigInteger();
    }

    private void calculateTotalCosts() {
        totalCosts = milestones.values().stream()
                .map(ProcurementMilestoneForm::getPayment)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
}
