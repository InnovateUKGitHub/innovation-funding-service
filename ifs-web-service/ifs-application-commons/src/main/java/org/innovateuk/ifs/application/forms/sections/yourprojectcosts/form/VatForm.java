package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.Vat;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NOT_BLANK_MESSAGE;

public class VatForm {

    private Long costId;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean registered;

    public VatForm() {
    }

    public VatForm(Vat vat) {
        this.costId = vat.getId();
        this.registered = vat.getRegistered();
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }
}
