package com.worth.ifs.project.financecheck.form;

import com.worth.ifs.project.finance.resource.FinanceCheckResource;

public class FinanceCheckForm {
    private FinanceCheckResource financeCheckResource;

    public FinanceCheckForm() {
    }

    public FinanceCheckForm(FinanceCheckResource financeCheckResource) {
        this.financeCheckResource = financeCheckResource;
    }

    public FinanceCheckResource getFinanceCheckResource() {
        return financeCheckResource;
    }

    public void setFinanceCheckResource(FinanceCheckResource financeCheckResource) {
        this.financeCheckResource = financeCheckResource;
    }
}
