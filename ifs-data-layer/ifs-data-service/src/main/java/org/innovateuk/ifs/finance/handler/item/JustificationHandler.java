package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Justification;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class JustificationHandler extends FinanceRowHandler<Justification> {

    public static final String COST_KEY = "justification";

    @Override
    public void validate(@NotNull Justification justification, @NotNull BindingResult bindingResult) {
        super.validate(justification, bindingResult);
    }

    @Override
    public Justification toResource(FinanceRow cost) {
        return new Justification(cost.getTarget().getId(), cost.getId(), null, null);
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(FinanceRowType.JUSTIFICATION);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(Justification justification) {
        return new ApplicationFinanceRow(justification.getId(), COST_KEY, justification.getExceedAllowedLimit() != null ? justification.getExceedAllowedLimit().toString() : "", justification.getExplanation(), 0, BigDecimal.ZERO, null, justification.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Justification justification) {
        return new ProjectFinanceRow(justification.getId(), COST_KEY, justification.getExceedAllowedLimit().toString(), justification.getExplanation(), 0, BigDecimal.ZERO, null, justification.getCostType());
    }

    @Override
    protected List<Justification> initialiseCosts(Finance finance) {
        return newArrayList(new Justification(finance.getId()));
    }
}
