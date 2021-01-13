package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;

public class ApplicationProcurementMilestoneSummaryViewModel {

    private Integer month;
    private String description;
    private BigInteger payment;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getPayment() {
        return payment;
    }

    public void setPayment(BigInteger payment) {
        this.payment = payment;
    }

}