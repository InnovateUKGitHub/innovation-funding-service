package org.innovateuk.ifs.project.finance.resource;

import java.time.LocalDate;

public class ProjectProcurementMilestoneResource {

    private PaymentMilestoneState paymentMilestoneState;

    private String paymentMilestoneInternalUserFirstName;
    private String paymentMilestoneInternalUserLastName;
    private LocalDate paymentMilestoneLastModifiedDate;

    public ProjectProcurementMilestoneResource() {
    }

    public ProjectProcurementMilestoneResource(PaymentMilestoneState paymentMilestoneState, String paymentMilestoneInternalUserFirstName, String paymentMilestoneInternalUserLastName, LocalDate paymentMilestoneLastModifiedDate) {
        this.paymentMilestoneState = paymentMilestoneState;
        this.paymentMilestoneInternalUserFirstName = paymentMilestoneInternalUserFirstName;
        this.paymentMilestoneInternalUserLastName = paymentMilestoneInternalUserLastName;
        this.paymentMilestoneLastModifiedDate = paymentMilestoneLastModifiedDate;
    }

    public PaymentMilestoneState getPaymentMilestoneState() {
        return paymentMilestoneState;
    }

    public void setPaymentMilestoneState(PaymentMilestoneState paymentMilestoneState) {
        this.paymentMilestoneState = paymentMilestoneState;
    }

    public String getPaymentMilestoneInternalUserFirstName() {
        return paymentMilestoneInternalUserFirstName;
    }

    public void setPaymentMilestoneInternalUserFirstName(String paymentMilestoneInternalUserFirstName) {
        this.paymentMilestoneInternalUserFirstName = paymentMilestoneInternalUserFirstName;
    }

    public String getPaymentMilestoneInternalUserLastName() {
        return paymentMilestoneInternalUserLastName;
    }

    public void setPaymentMilestoneInternalUserLastName(String paymentMilestoneInternalUserLastName) {
        this.paymentMilestoneInternalUserLastName = paymentMilestoneInternalUserLastName;
    }

    public LocalDate getPaymentMilestoneLastModifiedDate() {
        return paymentMilestoneLastModifiedDate;
    }

    public void setPaymentMilestoneLastModifiedDate(LocalDate paymentMilestoneLastModifiedDate) {
        this.paymentMilestoneLastModifiedDate = paymentMilestoneLastModifiedDate;
    }

    public boolean isMilestonePaymentApproved() {
        return this.getPaymentMilestoneState() == PaymentMilestoneState.APPROVED;
    }
}
