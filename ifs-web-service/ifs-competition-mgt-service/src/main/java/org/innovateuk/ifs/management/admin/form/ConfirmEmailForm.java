package org.innovateuk.ifs.management.admin.form;

import javax.validation.constraints.NotNull;

public class ConfirmEmailForm {

    @NotNull(message = "{validation.manage.users.email.change.confirmation}")
    private Boolean confirmation;

    public Boolean getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Boolean confirmation) {
        this.confirmation = confirmation;
    }
}
