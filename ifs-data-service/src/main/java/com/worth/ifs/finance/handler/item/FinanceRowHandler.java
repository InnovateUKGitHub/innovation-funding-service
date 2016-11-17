package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.validator.util.ValidationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(FinanceRowHandler.class);
    Map<String, FinanceRowMetaField> costFields = new HashMap<>();

    public abstract ApplicationFinanceRow toCost(FinanceRowItem costItem);

    public abstract FinanceRowItem toCostItem(ApplicationFinanceRow cost);

    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {
        ValidationUtil.isValid(bindingResult, costItem, (Class<?>[]) null);
    }

    protected void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult, Class<?>... classes) {
        ValidationUtil.isValid(bindingResult, costItem, classes);
    }

    public void setCostFields(List<FinanceRowMetaField> financeRowMetaFields) {
        this.costFields = financeRowMetaFields.stream().collect(Collectors.toMap(FinanceRowMetaField::getTitle, Function.<FinanceRowMetaField>identity()));
    }

    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        return costs;
    }
}
