package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.validator.OtherFundingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;

/**
 * Handles the other funding, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class OtherFundingHandler extends FinanceRowHandler<OtherFunding> {
    public static final String COST_KEY = "other-funding";
    public static final String OTHER_FUNDING_NAME = OTHER_FUNDING.getDisplayName();

    @Autowired
    private OtherFundingValidator validator;

    @Override
    public void validate(@NotNull OtherFunding otherFunding, @NotNull BindingResult bindingResult) {
        super.validate(otherFunding, bindingResult);
        validator.validate(otherFunding, bindingResult);
    }
    
    @Override
    public ApplicationFinanceRow toApplicationDomain(OtherFunding otherFunding) {
        return otherFunding != null ? mapOtherFunding(otherFunding) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(OtherFunding otherFunding) {
        return otherFunding != null ? mapOtherFundingToProjectCost(otherFunding) : null;
    }

    @Override
    public OtherFunding toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(OTHER_FUNDING);
    }

    private OtherFunding buildRowItem(FinanceRow cost) {
        if (OTHER_FUNDING_NAME.equals(cost.getDescription())) {
            return new OtherFunding(cost.getId(), cost.getItem(), cost.getDescription(), null, cost.getCost(), cost.getTarget().getId());
        }
        return new OtherFunding(cost.getId(), null, cost.getDescription(), cost.getItem(), cost.getCost(), cost.getTarget().getId());
    }

    private ApplicationFinanceRow mapOtherFunding(FinanceRowItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new ApplicationFinanceRow(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, otherFunding.getCostType());
    }

    private ProjectFinanceRow mapOtherFundingToProjectCost(FinanceRowItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new ProjectFinanceRow(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, otherFunding.getCostType());
    }

    @Override
    protected List<OtherFunding> intialiseCosts(Finance finance) {
        Long id = null;
        String otherPublicFunding;
        if (finance.getCompetition().isFullyFunded()) {
            otherPublicFunding = "No";
        } else {
            otherPublicFunding = "";
        }
        String fundingSource = OtherFundingCostCategory.OTHER_FUNDING;
        String securedDate = null;
        BigDecimal fundingAmount = new BigDecimal(0);
        return newArrayList(new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount, finance.getId()));
    }
}
