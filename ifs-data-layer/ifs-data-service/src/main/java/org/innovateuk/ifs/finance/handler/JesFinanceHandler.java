package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.handler.item.*;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class JesFinanceHandler extends AbstractOrganisationFinanceHandler implements OrganisationTypeFinanceHandler {
    private static final Log LOG = LogFactory.getLog(JesFinanceHandler.class);

    @Autowired
    private GrantClaimPercentageHandler grantClaimHandler;

    @Autowired
    private OtherFundingHandler otherFundingHandler;

    @Autowired
    private GrantClaimAmountHandler grantClaimAmountHandler;

    @Autowired
    private JESCostHandler jesCostHandler;

    @Override
    protected boolean initialiseCostTypeSupported(FinanceRowType costType) {
        return asList(FINANCE, GRANT_CLAIM_AMOUNT, OTHER_FUNDING).contains(costType);
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> createCostCategories(Competition competition, Finance finance) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);

        for (FinanceRowType costType : competition.getFinanceRowTypes()) {
            FinanceRowCostCategory financeRowCostCategory;
            switch (costType) {
                case FINANCE:
                case GRANT_CLAIM_AMOUNT:
                    financeRowCostCategory = new ExcludedCostCategory();
                    break;
                case OTHER_FUNDING:
                    financeRowCostCategory = new OtherFundingCostCategory();
                    break;
                default:
                    financeRowCostCategory = new DefaultCostCategory();

            }
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> afterTotalCalculation(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        return costCategories;
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler;
        switch (costType) {
            case LABOUR:
            case CAPITAL_USAGE:
            case MATERIALS:
            case OTHER_COSTS:
            case OVERHEADS:
            case SUBCONTRACTING_COSTS:
            case TRAVEL:
            case YOUR_FINANCE:
                handler = jesCostHandler;
                break;
            case FINANCE:
                handler = grantClaimHandler;
                break;
            case OTHER_FUNDING:
                handler = otherFundingHandler;
                break;
            case GRANT_CLAIM_AMOUNT:
                handler = grantClaimAmountHandler;
                break;
            default:
                handler = null;
        }
        if (handler != null) {
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(long projectFinanceId) {
        return noChangesAsAcademicFinancesAreNotEditable();
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair>> noChangesAsAcademicFinancesAreNotEditable() {
        return emptyMap();
    }

}