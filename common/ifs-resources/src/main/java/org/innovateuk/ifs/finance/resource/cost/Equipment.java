package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * {@code Equipment} implements {@link FinanceRowItem}
 */
public class Equipment extends AbstractFinanceRowItem {
    private Long id;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Size(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer quantity;

    public Equipment() {
        this(null);
    }

    public Equipment(Long targetId) {
        super(targetId);
    }

    public Equipment(Long id, String item, BigDecimal cost, Integer quantity, Long targetId) {
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

    public BigDecimal getCost() {
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
            total = cost.multiply(new BigDecimal(quantity));
        }
        return total;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.EQUIPMENT;
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
        return this.getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}