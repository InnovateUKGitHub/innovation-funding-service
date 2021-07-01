package org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.math.RoundingMode;

@Component
public class ProcurementMilestoneFormValidator {

    public void validate(ProcurementMilestonesForm form, BaseFinanceResource finance, ValidationHandler validationHandler) {
        form.getMilestones().forEach((id, milestone) -> {
            if (milestone.getPayment() == null || milestone.getPayment().compareTo(BigInteger.ZERO) < 0) {
                validationHandler.addError(Error.fieldError(String.format("milestones[%s].payment", id), milestone.getPayment(), "validation.procurement.milestones.payment"));
            }
        });
        if (!validationHandler.hasErrors()) {
            BigInteger funding = finance.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).toBigInteger();
            if (form.getTotalPayments().compareTo(funding) > 0) {
                validationHandler.addError(Error.fieldError("totalErrorHolder", form.getTotalPayments(), "validation.procurement.milestones.total.higher"));
            }
            if (form.getTotalPayments().compareTo(funding) < 0) {
                validationHandler.addError(Error.fieldError("totalErrorHolder", form.getTotalPayments(), "validation.procurement.milestones.total.lower"));
            }
        }
    }

}
