package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.GrantClaimHandler;
import org.innovateuk.ifs.finance.handler.item.JESCostHandler;
import org.innovateuk.ifs.finance.handler.item.OtherFundingHandler;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;

@Component
public class OrganisationJESFinance extends AbstractOrganisationFinanceHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationJESFinance.class);

    @Autowired
    private GrantClaimHandler grantClaimHandler;

    @Autowired
    private OtherFundingHandler otherFundingHandler;

    @Autowired
    private JESCostHandler jesCostHandler;

    @Override
    protected boolean initialiseCostTypeSupported(FinanceRowType costType) {
        return asList(FINANCE, OTHER_FUNDING).contains(costType);
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);

        for (FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory;
            switch (costType) {
                case FINANCE:
                    financeRowCostCategory = new GrantClaimCategory();
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
        FinanceRowHandler handler = null;
        switch (costType) {
            case LABOUR:
            case CAPITAL_USAGE:
            case MATERIALS:
            case OTHER_COSTS:
            case OVERHEADS:
            case SUBCONTRACTING_COSTS:
            case TRAVEL:
            case YOUR_FINANCE:
            case ACADEMIC:
                handler = jesCostHandler;
                break;
            case FINANCE:
                handler = grantClaimHandler;
                break;
            case OTHER_FUNDING:
                handler = otherFundingHandler;
                break;
        }
        if (handler != null) {
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        return noChangesAsAcademicFinancesAreNotEditable();
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair>> noChangesAsAcademicFinancesAreNotEditable() {
        return emptyMap();
    }

}