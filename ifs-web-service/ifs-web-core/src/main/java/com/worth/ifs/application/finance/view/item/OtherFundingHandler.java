package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import com.worth.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;

/**
 * Handles the conversion of form fields to other funding item
 */
public class OtherFundingHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        String otherPublicFunding = null;
        String fundingSource = null;
        String securedDate = null;
        BigDecimal fundingAmount = null;

        for (FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if (fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "otherPublicFunding":
                        fundingSource = OTHER_FUNDING;
                        otherPublicFunding = fieldValue;
                        break;
                    case "fundingAmount":
                        fundingAmount = NumberUtils.getBigDecimalValue(fieldValue, 0d);
                        break;
                    case "fundingSource":
                        fundingSource = fieldValue;
                        break;
                    case "securedDate":
                        securedDate = fieldValue;
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }

        return new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
    }
}
