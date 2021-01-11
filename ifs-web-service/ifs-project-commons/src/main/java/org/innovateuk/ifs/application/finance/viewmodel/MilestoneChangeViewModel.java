package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;

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

    public String getMonthVariance() {
        if (type == MilestoneChangeType.REMOVED) {
            return "Deleted";
        }
        if (type == MilestoneChangeType.ADDED) {
            return "Added";
        }
        int change = monthUpdated - monthSubmitted;
        String direction = (change > 0) ? "+" : "-";

        return direction + " " + change;
    }

    public String getPaymentVariance() {
        if (type == MilestoneChangeType.UPDATED) {
            BigInteger change = paymentUpdated.subtract(paymentSubmitted);
            String direction = (change.compareTo(new BigInteger("0")) > 0) ? "+" : "-";

            return direction + " " + change.abs();
        }
        return "0";
    }


    public enum MilestoneChangeType {
        ADDED, REMOVED, UPDATED;
    }
}


