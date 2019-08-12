package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class VAT extends AbstractFinanceRowItem {
    private Long id;
    private String name;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean registered;

    public VAT() {
        this(null);
    }

    public VAT(Long targetId) {
        super(targetId);
        this.name = getCostType().getType();
    }

    public VAT(Long id, String name, Boolean registered, Long targetId) {
        this(targetId);
        this.id = id;
        this.name = name;
        this.registered = registered;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal totalDiff(FinanceRowItem other) {
        return super.totalDiff(other);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.VAT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        VAT that = (VAT) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(registered, that.registered)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(registered)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("registered", registered)
                .toString();
    }
}
