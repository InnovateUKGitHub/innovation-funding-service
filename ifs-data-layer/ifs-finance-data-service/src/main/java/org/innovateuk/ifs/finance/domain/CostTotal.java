package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class CostTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long financeId;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @Column(precision = 15, scale = 6)
    private BigDecimal total;

    public CostTotal() {
    }

    public CostTotal(Long financeId, String name, String type, BigDecimal total) {
        this.financeId = financeId;
        this.name = name;
        this.type = type;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public Long getFinanceId() {
        return financeId;
    }

    public void setFinanceId(Long financeId) {
        this.financeId = financeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
