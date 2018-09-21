package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to a grant claims
 */
public class GrantClaimHandler extends FinanceRowHandler {
    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {

        Optional<FinanceFormField> grantClaimPercentageField = financeFormFields.stream().findFirst();

        Integer grantClaimPercentage = grantClaimPercentageField.
                map(this::convertFieldValueToInteger).
                orElse(0);

        if (allNull(id, grantClaimPercentage)) {
            return null;
        }
        return new GrantClaim(id, grantClaimPercentage);
    }

    private Integer convertFieldValueToInteger(FinanceFormField field) {

        String value = field.getValue();
        BigDecimal bigValue = NumberUtils.getBigDecimalValue(value, 0d);

        if (bigValue.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
            String startOfNumber = bigValue.toPlainString().substring(0, 9);
            return Integer.parseInt(startOfNumber);
        }
        return bigValue.intValue();
    }
}
