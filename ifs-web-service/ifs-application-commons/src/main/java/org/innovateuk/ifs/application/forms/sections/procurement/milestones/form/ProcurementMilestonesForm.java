package org.innovateuk.ifs.application.forms.sections.procurement.milestones.form;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class ProcurementMilestonesForm {
    public static final String UNSAVED_ROW_PREFIX = "unsaved-";

    public static String generateUnsavedRowId() {
        return UNSAVED_ROW_PREFIX + UUID.randomUUID().toString();
    }

    @Valid
    private Map<String, ProcurementMilestoneForm> milestones = new LinkedHashMap<>();

    private Object totalErrorHolder;

    public ProcurementMilestonesForm() {}

    public ProcurementMilestonesForm(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
        if (this.milestones.isEmpty()) {
            this.milestones.put(generateUnsavedRowId(), new ProcurementMilestoneForm("Milestone 1"));
        }
    }

    public Map<String, ProcurementMilestoneForm> getMilestones() {
        return milestones;
    }

    public void setMilestones(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
    }

    public Object getTotalErrorHolder() {
        return totalErrorHolder;
    }

    public void setTotalErrorHolder(Object totalErrorHolder) {
        this.totalErrorHolder = totalErrorHolder;
    }

    public BigInteger getTotalPayments() {
        return milestones.values().stream()
                .map(ProcurementMilestoneForm::getPayment)
                .filter(Objects::nonNull)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    public BigDecimal getTotalPercentages(BigInteger fundingAmount) {
        BigInteger totalPayments = getTotalPayments();
        if (fundingAmount.equals(BigInteger.ZERO)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(totalPayments)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(fundingAmount), 2, RoundingMode.HALF_UP);
    }

    public ProcurementMilestonesForm reorderMilestones() {
        milestones = milestones.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getMonth(), Comparator.nullsLast(Integer::compareTo)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return this;
    }
}
