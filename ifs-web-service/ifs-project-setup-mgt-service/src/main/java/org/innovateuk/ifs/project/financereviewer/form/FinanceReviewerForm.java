package org.innovateuk.ifs.project.financereviewer.form;

import javax.validation.constraints.NotNull;

public class FinanceReviewerForm {

    @NotNull
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
