package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class OtherCost extends AbstractFinanceRowItem {
    private Long id;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Size(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;


    private OtherCost() {
        this(null);
    }

    public OtherCost(Long targetId) {
        super(targetId);
    }

    public OtherCost(Long id, String description, BigDecimal cost, Long targetId) {
        this(targetId);
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.OTHER_COSTS;
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
