package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Materials;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class MaterialRowForm extends AbstractCostRowForm<Materials> {

    @NotNull(message = NOT_BLANK_MESSAGE)
    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer quantity;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;

    public MaterialRowForm() { }

    public MaterialRowForm(Materials cost) {
        super(cost);
        this.item = cost.getItem();
        this.quantity = cost.getQuantity();
        this.cost = cost.getCost();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && quantity == null && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.MATERIALS;
    }

    @Override
    public Materials toCost() {
        return new Materials(getCostId(), item, cost, quantity);
    }
}
