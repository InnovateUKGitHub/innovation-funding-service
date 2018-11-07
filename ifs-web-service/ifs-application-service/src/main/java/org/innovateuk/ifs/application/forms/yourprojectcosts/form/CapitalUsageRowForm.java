package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.CapitalUsage;

import java.math.BigDecimal;

public class CapitalUsageRowForm extends AbstractCostRowForm<CapitalUsage> {

    private String item;

    private Boolean newItem;

    private Integer deprecation;

    private BigDecimal netValue;

    private BigDecimal residualValue;

    private Integer utilisation;

    public CapitalUsageRowForm() {}

    public CapitalUsageRowForm(CapitalUsage cost) {
        super(cost);
        this.item = cost.getDescription();
        this.newItem = cost.getExisting().equals("No");
        this.deprecation = cost.getDeprecation();
        this.netValue = cost.getNpv();
        this.residualValue = cost.getResidualValue();
        this.utilisation = cost.getUtilisation();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getNewItem() {
        return newItem;
    }

    public void setNewItem(Boolean newItem) {
        this.newItem = newItem;
    }

    public Integer getDeprecation() {
        return deprecation;
    }

    public void setDeprecation(Integer deprecation) {
        this.deprecation = deprecation;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public void setNetValue(BigDecimal netValue) {
        this.netValue = netValue;
    }

    public BigDecimal getResidualValue() {
        return residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
        this.residualValue = residualValue;
    }

    public Integer getUtilisation() {
        return utilisation;
    }

    public void setUtilisation(Integer utilisation) {
        this.utilisation = utilisation;
    }

    public BigDecimal getTotal() {
        // ( npv - residualValue ) * utilisation-percentage
        if (netValue == null || residualValue == null || utilisation == null) {
            return BigDecimal.ZERO;
        }

        return netValue.subtract(residualValue)
                .multiply(new BigDecimal(utilisation)
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN));
    }

}
