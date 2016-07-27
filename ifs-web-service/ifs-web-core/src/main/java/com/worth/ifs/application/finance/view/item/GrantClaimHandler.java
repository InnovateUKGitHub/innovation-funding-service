package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.util.NumberUtils;

import java.util.List;
import java.util.Optional;

/**
 * Handles the conversion of form fields to a grant claims
 */
public class GrantClaimHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        Optional<FinanceFormField> grantClaimPercentageField = financeFormFields.stream().findFirst();
        Integer grantClaimPercentage = 0;
        if (grantClaimPercentageField.isPresent()) {
            grantClaimPercentage = NumberUtils.getIntegerValue(grantClaimPercentageField.get().getValue(), 0);
        }

        return new GrantClaim(id, grantClaimPercentage);
    }

}
