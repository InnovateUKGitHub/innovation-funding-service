package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.cost.OtherFunding;
import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;

import java.math.BigDecimal;
import java.util.List;

public class OtherFundingHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        String otherPublicFunding = null;
        String fundingSource = null;
        String dateSecured = null;
        BigDecimal fundingAmount = null;

        for (CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "otherPublicFunding":
                        otherPublicFunding = fieldValue;
                        break;
                    case "fundingAmount":
                        fundingAmount = getBigDecimalValue(fieldValue, 0d);
                        break;
                    case "fundingSource":
                        fundingSource = fieldValue;
                        break;
                    case "dateSecured":
                        dateSecured = fieldValue;
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }

        return new OtherFunding(id, otherPublicFunding, fundingSource, dateSecured, fundingAmount);
    }
}
