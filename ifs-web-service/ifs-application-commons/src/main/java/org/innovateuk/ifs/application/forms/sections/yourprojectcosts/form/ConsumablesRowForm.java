package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.Consumable;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.*;
import java.math.BigInteger;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class ConsumablesRowForm extends AbstractCostRowForm<Consumable> {

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
    private BigInteger cost;

    public ConsumablesRowForm() { }

    public ConsumablesRowForm(Consumable cost) {
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

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && quantity == null && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.CONSUMABLES;
    }

    @Override
    public Consumable toCost(Long financeId) {
        return new Consumable(getCostId(), item, cost, quantity, financeId);
    }
}
