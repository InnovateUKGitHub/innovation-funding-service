package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.error.ValidationUtil;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

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
        return intialiseCosts(applicationFinance)
                .stream()
                .map(this::toApplicationDomain)
                .collect(Collectors.toList());
    }

    public List<ProjectFinanceRow> initializeCost(ProjectFinance projectFinance) {
        return intialiseCosts(projectFinance)
                .stream()
                .map(this::toProjectDomain)
                .collect(Collectors.toList());
    }

    protected List<T> intialiseCosts(Finance finance) {
        return emptyList();
    }

    protected BigInteger bigIntegerOrNull(BigDecimal cost) {
        return ofNullable(cost).map(BigDecimal::toBigInteger).orElse(null);
    }
}
