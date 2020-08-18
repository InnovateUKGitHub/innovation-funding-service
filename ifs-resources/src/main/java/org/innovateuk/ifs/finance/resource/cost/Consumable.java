package org.innovateuk.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * {@code Materials} implements {@link FinanceRowItem}
 */
public class Consumable extends AbstractFinanceRowItem {
    private Long id;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    private BigInteger cost;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer quantity;

    private Consumable() {
        this(null);
    }

    public Consumable(Long targetId) {
        super(targetId);
    }

    public Consumable(Long id, String item, BigInteger cost, Integer quantity, Long targetId) {
        this(targetId);
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

    public BigInteger getCost() {
        return cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getTotal() {
        // calculated, no validation
        BigDecimal total = BigDecimal.ZERO;
        if (quantity != null && cost != null) {
            total = new BigDecimal(cost.multiply(BigInteger.valueOf(quantity)));
        }
        return total;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.CONSUMABLES;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getName() {
        return this.getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
