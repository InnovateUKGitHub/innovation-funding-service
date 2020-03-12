package org.innovateuk.ifs.finance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DB_STRING_LENGTH;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_LENGTH_MESSAGE;

/**
 * FinanceRow defines database relations and a model to use client side and server side.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "row_type", discriminatorType = DiscriminatorType.STRING)
public abstract class FinanceRow<FinanceType extends Finance> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = MAX_DB_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @Length(max = MAX_DB_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    private Integer quantity;
    private BigDecimal cost;

    @Length(max = MAX_DB_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String name;

    @OneToMany(mappedBy = "financeRowId", cascade = REMOVE)
    private List<FinanceRowMetaValue> financeRowMetadata = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private FinanceRowType type;

    public FinanceRow() {
    }

    /**
     * Constructor used to add a new and empty cost object.
     */
    public FinanceRow(FinanceRowType type) {
        this.name = "";
        this.item = "";
        this.description = "";
        this.quantity = null;
        this.cost = null;
        this.type = type;
    }

    public FinanceRow(String name, String item, String description, Integer quantity, BigDecimal cost, FinanceRowType financeRowType) {
        this.name = name;
        this.item = item;
        this.description = description;
        this.quantity = quantity;
        this.cost = cost;
        this.type = financeRowType;
    }

    public FinanceRow(Long id, String name, String item, String description, Integer quantity, BigDecimal cost, FinanceRowType financeRowType) {
        this(name, item, description, quantity, cost, financeRowType);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(name) && name.length() > MAX_DB_STRING_LENGTH) ? name.substring(0, MAX_DB_STRING_LENGTH) : name;
    }

    public String getItem() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(item) && item.length() > MAX_DB_STRING_LENGTH) ? item.substring(0, MAX_DB_STRING_LENGTH) : item;
    }

    public String getDescription() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(description) && description.length() > MAX_DB_STRING_LENGTH) ? description.substring(0, MAX_DB_STRING_LENGTH) : description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public List<FinanceRowMetaValue> getFinanceRowMetadata() {
        return financeRowMetadata;
    }

    public void setFinanceRowMetadata(List<FinanceRowMetaValue> costValues) {
        this.financeRowMetadata = costValues;
    }

    public void addCostValues(FinanceRowMetaValue... c) {
        Collections.addAll(this.financeRowMetadata, c);
    }

    public void setName(String name) {
        this.name = name;
    }

    public FinanceRowType getType() {
        return type;
    }

    public void setType(FinanceRowType type) {
        this.type = type;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public abstract void setTarget(FinanceType target);

    public abstract FinanceType getTarget();

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Used for comparing application and project finance rows.  Doesn't consider associated meta fields.
     *
     * @param another
     * @return
     */
    public boolean matches(FinanceRow another) {
        if (another == null) return false;

        return new EqualsBuilder()
                .append(getItem(), another.getItem())
                .append(getCost(), another.getCost())
                .append(getDescription(), another.getDescription())
                .append(getName(), another.getName())
                .append(getQuantity(), another.getQuantity())
                .isEquals();
    }
}
