package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.error.ValidationUtil;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FinanceRowHandler<T extends FinanceRowItem> {

    Map<String, FinanceRowMetaField> costFields = new HashMap<>();

    public abstract ApplicationFinanceRow toApplicationDomain(T costItem);

    public abstract ProjectFinanceRow toProjectDomain(T costItem);

    public abstract T toResource(FinanceRow cost);

    public abstract FinanceRowType getFinanceRowType();

    public void validate(@NotNull T costItem, @NotNull BindingResult bindingResult) {
        ValidationUtil.isValid(bindingResult, costItem, (Class<?>[]) null);
    }

    protected void validate(@NotNull T costItem, @NotNull BindingResult bindingResult, Class<?>... classes) {
        ValidationUtil.isValid(bindingResult, costItem, classes);
    }

    public void setCostFields(List<FinanceRowMetaField> financeRowMetaFields) {
        this.costFields = financeRowMetaFields.stream().collect(Collectors.toMap(FinanceRowMetaField::getTitle, Function.<FinanceRowMetaField>identity()));
    }

    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        return new ArrayList<>();
    }
}
