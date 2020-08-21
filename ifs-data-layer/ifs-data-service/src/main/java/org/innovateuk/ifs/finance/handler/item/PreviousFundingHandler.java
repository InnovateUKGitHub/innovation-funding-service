package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.BaseOtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;
import org.innovateuk.ifs.finance.validator.PreviousFundingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.handler.item.OtherFundingHandler.OTHER_FUNDING_NAME;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.PREVIOUS_FUNDING;

/**
 * Handles the previous funding, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class PreviousFundingHandler extends FinanceRowHandler<PreviousFunding> {
    public static final String COST_KEY = "other-funding";

    @Autowired
    private PreviousFundingValidator validator;

    @Override
    public void validate(@NotNull PreviousFunding previousFunding, @NotNull BindingResult bindingResult) {
        super.validate(previousFunding, bindingResult);
        validator.validate(previousFunding, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(PreviousFunding previousFunding) {
        return previousFunding != null ? mapPreviousFunding(previousFunding) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(PreviousFunding previousFunding) {
        return previousFunding != null ? mapPreviousFundingToProjectCost(previousFunding) : null;
    }

    @Override
    public PreviousFunding toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(PREVIOUS_FUNDING);
    }

    private PreviousFunding buildRowItem(FinanceRow cost) {
        if (OTHER_FUNDING_NAME.equals(cost.getDescription())) {
            return new PreviousFunding(cost.getId(), cost.getItem(), cost.getDescription(), null, cost.getCost(), cost.getTarget().getId());
        }
        return new PreviousFunding(cost.getId(), null, cost.getDescription(), cost.getItem(), cost.getCost(), cost.getTarget().getId());
    }

    private ApplicationFinanceRow mapPreviousFunding(FinanceRowItem costItem) {
        PreviousFunding previousFunding = (PreviousFunding) costItem;
        String item;
        if (previousFunding.getOtherPublicFunding() != null) {
            item = previousFunding.getOtherPublicFunding();
        } else {
            item = previousFunding.getSecuredDate();
        }
        return new ApplicationFinanceRow(previousFunding.getId(), COST_KEY, item, previousFunding.getFundingSource(), 0, previousFunding.getFundingAmount(), null, previousFunding.getCostType());
    }

    private ProjectFinanceRow mapPreviousFundingToProjectCost(FinanceRowItem costItem) {
        PreviousFunding previousFunding = (PreviousFunding) costItem;
        String item;
        if (previousFunding.getOtherPublicFunding() != null) {
            item = previousFunding.getOtherPublicFunding();
        } else {
            item = previousFunding.getSecuredDate();
        }
        return new ProjectFinanceRow(previousFunding.getId(), COST_KEY, item, previousFunding.getFundingSource(), 0, previousFunding.getFundingAmount(), null, previousFunding.getCostType());
    }

    @Override
    protected List<PreviousFunding> initialiseCosts(Finance finance) {
        Long id = null;
        String receivedOtherFunding;
        if (finance.getCompetition().isFullyFunded()) {
            receivedOtherFunding = "No";
        } else {
            receivedOtherFunding = "";
        }
        String fundingSource = BaseOtherFundingCostCategory.OTHER_FUNDING;
        String securedDate = null;
        BigDecimal fundingAmount = new BigDecimal(0);
        return newArrayList(new PreviousFunding(id, receivedOtherFunding, fundingSource, securedDate, fundingAmount, finance.getId()));
    }
}
