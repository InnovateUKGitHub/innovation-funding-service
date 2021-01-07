package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;

public class MilestoneChangeViewModel {
    private String description;
    private Integer monthSubmitted;
    private Integer monthUpdated;
    private BigInteger paymentSubmitted;
    private BigInteger paymentUpdated;
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


    public enum MilestoneChangeType {
        ADDED, REMOVED, UPDATED;
    }
}


