package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

public class ProcurementOverhead extends AbstractFinanceRowItem {

    public final static String FINANCE_OVERHEAD_FILE_REQUIRED = "{validation.finance.overhead.file.required}";

    public interface RateNotZero{}
    public interface TotalCost{}
    private Long id;
    private BigDecimal companyCost;
    private BigDecimal projectCost;
    private String item;
    private String name;

    public ProcurementOverhead(Long id, BigDecimal companyCost, BigDecimal projectCost) {
        this.id = id;
        this.companyCost = companyCost;
        this.projectCost = projectCost;
        this.name = getCostType().getType();
    }

    public ProcurementOverhead(Long id, BigDecimal companyCost, BigDecimal projectCost, String item) {
        this.id = id;
        this.companyCost = companyCost;
        this.projectCost = projectCost;
        this.item = item;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public FinanceRowType getCostType() {
        return  FinanceRowType.OVERHEADS;
    }

    @Override
    public String getName() {
        return name;
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCompanyCost() {
        return companyCost;
    }

    public void setCompanyCost(BigDecimal companyCost) {
        this.companyCost = companyCost;
    }

    public BigDecimal getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(BigDecimal projectCost) {
        this.projectCost = projectCost;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}