package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;

public class ApplicationProcurementMilestoneSummaryViewModel {

    private final Integer month;
    private final String description;
    private final BigInteger payment;

    public ApplicationProcurementMilestoneSummaryViewModel(Integer month, String description, BigInteger payment) {
        this.month = month;
        this.description = description;
        this.payment = payment;
    }
    public Integer getMonth() {
        return month;
    }

    public String getDescription() {
        return description;
    }

    public BigInteger getPayment() {
        return payment;
    }
}