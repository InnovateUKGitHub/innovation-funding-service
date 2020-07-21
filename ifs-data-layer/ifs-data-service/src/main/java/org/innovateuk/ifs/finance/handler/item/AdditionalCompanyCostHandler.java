package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost.AdditionalCompanyCostType;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ADDITIONAL_COMPANY_COSTS;

/**
 * Handles the associate salary costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class AdditionalCompanyCostHandler extends FinanceRowHandler<AdditionalCompanyCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(AdditionalCompanyCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AdditionalCompanyCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public AdditionalCompanyCost toResource(FinanceRow cost) {
        return new AdditionalCompanyCost(cost.getTarget().getId(), cost.getId(), AdditionalCompanyCostType.valueOf(cost.getName()), cost.getDescription(), ofNullable(cost.getCost()).map(BigDecimal::toBigInteger).orElse(null));
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(ADDITIONAL_COMPANY_COSTS);
    }

    @Override
    protected List<AdditionalCompanyCost> intialiseCosts(Finance finance) {
        return Arrays.stream(AdditionalCompanyCostType.values())
                .map(type -> new AdditionalCompanyCost(finance.getId(), type))
                .collect(Collectors.toList());
    }
}
