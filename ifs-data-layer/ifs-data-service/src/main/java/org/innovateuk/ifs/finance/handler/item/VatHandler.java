package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Vat;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class VatHandler extends FinanceRowHandler<Vat> {
    public static final String COST_KEY = "vat";

    @Override
    public void validate(@NotNull Vat vat, @NotNull BindingResult bindingResult) {
        super.validate(vat, bindingResult);
    }

    @Override
    public Vat toResource(FinanceRow cost) {
        return new Vat(cost.getId(), cost.getItem() == null ? null : Boolean.valueOf(cost.getItem()), cost.getCost(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(FinanceRowType.VAT);
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
    protected List<Vat> intialiseCosts(Finance finance) {
        return newArrayList(new Vat(null, null, new BigDecimal("0.2"), finance.getId()));
    }
}