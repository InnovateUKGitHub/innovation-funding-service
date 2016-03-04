package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.application.finance.view.item.CostHandler;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class JESFinanceFormHandler implements FinanceFormHandler {

    @Override
    public void update(HttpServletRequest request, Long userId, Long applicationId) {

    }

    @Override
    public void storeCost(Long userId, Long applicationId, String fieldName, String value) {
        if (fieldName != null && value != null) {
            String cleanedFieldName = fieldName;
            if (fieldName.startsWith("cost-")) {
                cleanedFieldName = fieldName.replace("cost-", "");
            }
            storeField(cleanedFieldName, value, userId, applicationId);
        }
    }

    private void storeField(String fieldName, String value, Long userId, Long applicationId) {
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        CostType costType = CostType.fromString(financeFormField.getKeyType());
        CostHandler costHandler = Acade
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !financeFormField.getId().equals("null")) {
            costFormFieldId = Long.parseLong(financeFormField.getId());
        }
        CostItem costItem = costHandler.toCostItem(costFormFieldId, Arrays.asList(financeFormField));
        storeCostItem(costItem, userId, applicationId, financeFormField.getQuestionId());
    }

    private FinanceFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");
        if (keyParts.length == 3) {
            return new FinanceFormField(costTypeKey, value, null, keyParts[2], keyParts[1], keyParts[0]);
        }
        return null;
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value) {

    }

    @Override
    public CostItem addCost(Long applicationId, Long userId, Long questionId) {
        return null;
    }
}
