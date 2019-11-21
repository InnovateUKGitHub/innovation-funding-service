package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.error.ValidationUtil;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

public abstract class FinanceRowHandler<T extends FinanceRowItem> {

    Map<String, FinanceRowMetaField> costFields = new HashMap<>();

    public abstract ApplicationFinanceRow toApplicationDomain(T costItem);

    public abstract ProjectFinanceRow toProjectDomain(T costItem);

    public abstract T toResource(FinanceRow cost);

    public abstract Optional<FinanceRowType> getFinanceRowType();

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
        return intialiseCost(applicationFinance)
                .map(this::toApplicationDomain)
                .map(Arrays::asList)
                .orElse(emptyList());
    }
    public List<ProjectFinanceRow> initializeCost(ProjectFinance projectFinance) {
        return intialiseCost(projectFinance)
                .map(this::toProjectDomain)
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    protected Optional<T> intialiseCost(Finance finance) {
        return empty();
    }
}
