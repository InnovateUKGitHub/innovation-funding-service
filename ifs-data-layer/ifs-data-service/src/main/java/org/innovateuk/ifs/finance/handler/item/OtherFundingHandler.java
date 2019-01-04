package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.validator.OtherFundingValidator;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;

/**
 * Handles the other funding, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OtherFundingHandler extends FinanceRowHandler<OtherFunding> {
    public static final String COST_KEY = "other-funding";
    public static final String OTHER_FUNDING_NAME = OTHER_FUNDING.getName();

    @Autowired
    private OtherFundingValidator validator;

    //TODO remove IFS-4982
    @Autowired
    private PublicContentRepository publicContentRepository;

    @Override
    public void validate(@NotNull OtherFunding otherFunding, @NotNull BindingResult bindingResult) {
        super.validate(otherFunding, bindingResult);
        validator.validate(otherFunding, bindingResult);
    }
    
    @Override
    public ApplicationFinanceRow toCost(OtherFunding otherFunding) {
        return otherFunding != null ? mapOtherFunding(otherFunding) : null;
    }

    @Override
    public ProjectFinanceRow toProjectCost(OtherFunding otherFunding) {
        return otherFunding != null ? mapOtherFundingToProjectCost(otherFunding) : null;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost) {
        if (OTHER_FUNDING_NAME.equals(cost.getDescription())) {
            return new OtherFunding(cost.getId(), cost.getItem(), cost.getDescription(), null, cost.getCost());
        }
        return new OtherFunding(cost.getId(), null, cost.getDescription(), cost.getItem(), cost.getCost());
    }

    private ApplicationFinanceRow mapOtherFunding(FinanceRowItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new ApplicationFinanceRow(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, null);
    }

    private ProjectFinanceRow mapOtherFundingToProjectCost(FinanceRowItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new ProjectFinanceRow(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, null);
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeOtherFunding(applicationFinance.getApplication().getCompetition()));
        return costs;
    }

    private ApplicationFinanceRow initializeOtherFunding(Competition competition) {
        Long id = null;
        String otherPublicFunding;
        if (publicContentRepository.findByCompetitionId(competition.getId()).getFundingType() == FundingType.PROCUREMENT) {
            otherPublicFunding = "No";
        } else {
            otherPublicFunding = "";
        }
        String fundingSource = OtherFundingCostCategory.OTHER_FUNDING;
        String securedDate = null;
        BigDecimal fundingAmount = new BigDecimal(0);
        OtherFunding costItem = new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
        return toCost(costItem);
    }
}
