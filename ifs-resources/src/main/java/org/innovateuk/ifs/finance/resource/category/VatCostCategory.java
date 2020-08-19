package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VatCostCategory implements FinanceRowCostCategory {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal totalCostsWithoutVat = BigDecimal.ZERO;

    @Override
    public List<FinanceRowItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        Optional<Vat> vat = costs.stream().findAny().map(Vat.class::cast);
        if (vat.map(Vat::getRegistered).orElse(false)) {
            total = totalCostsWithoutVat.multiply(vat.get().getRate());
        } else {
            total = BigDecimal.ZERO;
        }
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if(costItem!=null) {
            costs.add(costItem);
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return false;
    }

    public void setCosts(List<FinanceRowItem> costItems) {
        costs = costItems;
    }

    public void setTotalCostsWithoutVat(BigDecimal totalCostsWithoutVat) {
        this.totalCostsWithoutVat = totalCostsWithoutVat;
    }
}
