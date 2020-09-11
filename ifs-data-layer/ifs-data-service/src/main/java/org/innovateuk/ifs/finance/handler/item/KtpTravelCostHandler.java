package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost.KtpTravelCostType;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.KTP_TRAVEL;

/**
 * Handles the ktp travel costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class KtpTravelCostHandler extends FinanceRowHandler<KtpTravelCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(KtpTravelCost travel) {
        return new ApplicationFinanceRow(travel.getId(), null, ofNullable(travel.getType()).map(KtpTravelCostType::name).orElse(null), travel.getDescription(), travel.getQuantity(), travel.getCost(), null, travel.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(KtpTravelCost travel) {
        return new ProjectFinanceRow(travel.getId(), null, ofNullable(travel.getType()).map(KtpTravelCostType::name).orElse(null), travel.getDescription(), travel.getQuantity(), travel.getCost(), null, travel.getCostType());
    }

    @Override
    public KtpTravelCost toResource(FinanceRow cost) {
        return new KtpTravelCost(cost.getId(), ofNullable(cost.getItem()).map(KtpTravelCostType::valueOf).orElse(null), cost.getDescription(), cost.getCost(), cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(KTP_TRAVEL);
    }
}
