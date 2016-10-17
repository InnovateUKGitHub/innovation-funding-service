package com.worth.ifs.project.financecheck.form;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CostFormField {

    private Long id;

    @NotNull(message = "{validation.project.financecheck.required}")
    private BigDecimal value;

    private String costCategoryName;

    public CostFormField() {
    }

    public CostFormField(Long id, BigDecimal value, String costCategoryName) {
        this.id = id;
        this.value = value;
        this.costCategoryName = costCategoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCostCategoryName() {
        return costCategoryName;
    }

    public void setCostCategoryName(String costCategoryName) {
        this.costCategoryName = costCategoryName;
    }
}


