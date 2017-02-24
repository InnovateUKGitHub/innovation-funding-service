package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FinanceRowHandler<T> {

    Map<String, FinanceRowMetaField> costFields = new HashMap<>();

    public abstract ApplicationFinanceRow toCost(T costItem);

    public abstract ProjectFinanceRow toProjectCost(T costItem);

    public abstract FinanceRowItem toCostItem(ApplicationFinanceRow cost);

    public abstract FinanceRowItem toCostItem(ProjectFinanceRow cost);

    public void validate(@NotNull T costItem, @NotNull BindingResult bindingResult) {
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
