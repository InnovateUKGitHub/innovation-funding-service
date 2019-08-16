package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Vat;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class VatHandler extends FinanceRowHandler<Vat> {
    public static final String COST_KEY = "vat";

    @Override
    public void validate(@NotNull Vat vat, @NotNull BindingResult bindingResult) {
        super.validate(vat, bindingResult);
    }

    @Override
    public FinanceRowItem toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new Vat(cost.getId(), cost.getItem() == null ? null : Boolean.valueOf(cost.getItem()), cost.getCost(), cost.getTarget().getId());
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(Vat vat) {
        return new ApplicationFinanceRow(vat.getId(), COST_KEY, vat.getRegistered() != null ? vat.getRegistered().toString() : null, vat.getName(), 0, vat.getRate(), null, vat.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Vat vat) {
        return new ProjectFinanceRow(vat.getId(), COST_KEY, vat.getRegistered() != null ? vat.getRegistered().toString() : null, vat.getName(), 0, vat.getRate(), null, vat.getCostType());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(toApplicationDomain(new Vat(null, null, new BigDecimal("0.2"), applicationFinance.getId())));
        return costs;
    }
}