package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

public class ProcurementOverhead extends AbstractFinanceRowItem {

    public final static String FINANCE_OVERHEAD_FILE_REQUIRED = "{validation.finance.overhead.file.required}";

    public interface RateNotZero{}
    public interface TotalCost{}
    private Long id;
    private Integer companyCost;
    private BigDecimal projectCost;
    private String item;
    private String name;

    public ProcurementOverhead(Long targetId) {
        super(targetId);
        this.name = getCostType().getType();
    }

    public ProcurementOverhead(Long targetId, Long id, Integer companyCost, BigDecimal projectCost, String item, String name) {
        super(targetId);
        this.id = id;
        this.companyCost = companyCost;
        this.projectCost = projectCost;
        this.item = item;
        this.name = name;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.PROCUREMENT_OVERHEADS;
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

    public Integer getCompanyCost() {
        return companyCost;
    }

    public void setCompanyCost(Integer companyCost) {
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