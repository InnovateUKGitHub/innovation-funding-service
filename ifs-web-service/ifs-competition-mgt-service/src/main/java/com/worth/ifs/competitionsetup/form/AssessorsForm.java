package com.worth.ifs.competitionsetup.form;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Form for the assessors competition setup section.
 */
public class AssessorsForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.assessorsform.assessorCount.required}")
    private Integer assessorCount;

    @Min(value=0, message = "{validation.assessorsform.assessorPay.min}")
    @NotNull(message = "{validation.assessorsform.assessorPay.required}")
    @Digits(integer = 8, fraction = 2, message = "{validation.assessorsform.assessorPay.max.amount.invalid}")
    private BigDecimal assessorPay;

    public Integer getAssessorCount() {
        return assessorCount;
    }

    public void setAssessorCount(Integer assessorCount) {
        this.assessorCount = assessorCount;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public void setAssessorPay(BigDecimal assessorPay) {
        this.assessorPay = assessorPay;
    }
}
