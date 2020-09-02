package org.innovateuk.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * {@code KtpTravelCost} implements {@link FinanceRowItem}
 */
public class KtpTravelCost extends AbstractFinanceRowItem {
    public enum KtpTravelCostType {
        ASSOCIATE,
        SUPERVISOR
    }

    private Long id;

    @NotNull
    private KtpTravelCostType type;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer quantity;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;

    private KtpTravelCost() {
        this(null);
    }

    public KtpTravelCost(Long targetId) {
        super(targetId);
    }

    public KtpTravelCost(Long id, KtpTravelCostType type, String description, BigDecimal cost, Integer quantity, Long targetId) {
        this(targetId);
        this.id = id;
        this.type = type;
        this.description = description;
        this.cost = cost;
        this.quantity = quantity;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getTotal() {
        if (cost == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return cost.multiply(new BigDecimal(quantity));
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.TRAVEL;
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public KtpTravelCostType getType() {
        return type;
    }

    public void setType(KtpTravelCostType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
