package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.util.NumberUtils;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to a grant claims
 */
public class GrantClaimHandler extends FinanceRowHandler {
    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        Optional<FinanceFormField> grantClaimPercentageField = financeFormFields.stream().findFirst();
        Integer grantClaimPercentage = 0;
        if (grantClaimPercentageField.isPresent()) {
            grantClaimPercentage = NumberUtils.getIntegerValue(grantClaimPercentageField.get().getValue(), 0);
        }

        if(allNull(id, grantClaimPercentage)) {
        	return null;
        }
        return new GrantClaim(id, grantClaimPercentage);
    }

}
