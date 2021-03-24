package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;
import java.text.NumberFormat;

public class MilestoneChangeViewModel {
    private String description;
    private Integer monthSubmitted = 0;
    private Integer monthUpdated = 0;
    private BigInteger paymentSubmitted = BigInteger.ZERO;
    private BigInteger paymentUpdated = BigInteger.ZERO;
    private MilestoneChangeType type;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMonthSubmitted(Integer monthSubmitted) {
        this.monthSubmitted = monthSubmitted;
    }

    public Integer getMonthSubmitted() {
        return monthSubmitted;
    }

    public void setMonthUpdated(Integer monthUpdated) {
        this.monthUpdated = monthUpdated;
    }

    public Integer getMonthUpdated() {
        return monthUpdated;
    }

    public BigInteger getPaymentSubmitted() {
        return paymentSubmitted;
    }

    public void setPaymentSubmitted(BigInteger paymentSubmitted) {
        this.paymentSubmitted = paymentSubmitted;
    }

    public BigInteger getPaymentUpdated() {
        return paymentUpdated;
    }

    public void setPaymentUpdated(BigInteger paymentUpdated) {
        this.paymentUpdated = paymentUpdated;
    }

    public MilestoneChangeType getType() {
        return type;
    }

    public void setType(MilestoneChangeType type) {
        this.type = type;
    }

    public boolean isUpdated() {
        return MilestoneChangeType.UPDATED == type;
    }

    public boolean isAdded() {
        return MilestoneChangeType.ADDED == type;
    }

    public boolean isRemoved() {
        return MilestoneChangeType.REMOVED == type;
    }

    public boolean isSame() {
        return MilestoneChangeType.SAME == type;
    }

    public String getMonthVariance() {
        if (type == MilestoneChangeType.REMOVED) {
            return "Deleted";
        }
        if (type == MilestoneChangeType.ADDED) {
            return "Added";
        }
        int change = monthUpdated - monthSubmitted;
        if (change == 0) {
            return "";
        }
        String direction = (change > 0) ? "+ " : "- ";

        return direction + Math.abs(change);
    }

    public String getPaymentVariance() {
        if (type == MilestoneChangeType.UPDATED) {
            BigInteger change = paymentUpdated.subtract(paymentSubmitted);
            String direction = direction(change);

            return direction + NumberFormat.getNumberInstance().format(change.abs());
        }
        return "0";
    }

    private String direction(BigInteger change) {
        int comparison = change.compareTo(BigInteger.ZERO);
        if (comparison == 0) {
            return "";
        }
        if (comparison > 0) {
            return "+ ";
        }
        return "- ";
    }


    public enum MilestoneChangeType {
        SAME, ADDED, REMOVED, UPDATED;
    }
}


