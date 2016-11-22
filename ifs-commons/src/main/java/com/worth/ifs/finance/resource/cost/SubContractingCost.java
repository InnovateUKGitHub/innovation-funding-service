package com.worth.ifs.finance.resource.cost;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * {@code SubContractingCost} implements {@link FinanceRowItem}
 */
public class SubContractingCost implements FinanceRowItem {
    private Long id;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION)
    private BigDecimal cost;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String country;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String name;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String role;

    public SubContractingCost(){
    }

    public SubContractingCost(Long id, BigDecimal cost, String country, String name, String role) {
        this();
        this.id = id;
        this.cost = cost;
        this.country = country;
        this.name = name;
        this.role = role;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCountry() {

        return (StringUtils.length(country) >  MAX_DB_STRING_LENGTH ? country.substring(0, MAX_DB_STRING_LENGTH) : country);
    }

    @Override
    public String getName() {
        return (StringUtils.length(name) >  MAX_DB_STRING_LENGTH ? name.substring(0, MAX_DB_STRING_LENGTH) : name);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    public String getRole() {
        return (StringUtils.length(role) >  MAX_DB_STRING_LENGTH ? role.substring(0, MAX_DB_STRING_LENGTH) : role);
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.SUBCONTRACTING_COSTS;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
