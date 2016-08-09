package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * {@code Materials} implements {@link FinanceRowItem}
 */
public class Materials implements FinanceRowItem {
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    private String name;

    @NotNull
    @DecimalMin(value = "1")
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION)
    private BigDecimal cost;

    @NotNull
    @Min(1)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0)
    private Integer quantity;

    private BigDecimal total = BigDecimal.ZERO; // calculated, no validation

    public Materials() {
        this.name = getCostType().getType();
    }

    public Materials(Long id, String item, BigDecimal cost, Integer quantity) {
        this();
        this.id = id;
        this.item = item;
        this.cost = cost;
        this.quantity = quantity;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getTotal() {
        if(quantity!=null && cost!=null) {
            total = cost.multiply(new BigDecimal(quantity));
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.MATERIALS;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }
}
