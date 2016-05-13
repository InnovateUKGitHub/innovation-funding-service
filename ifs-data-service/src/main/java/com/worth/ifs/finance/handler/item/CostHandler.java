package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
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

public abstract class CostHandler {
    private static final Log LOG = LogFactory.getLog(CostHandler.class);
    Map<String, CostField> costFields = new HashMap<>();

    public abstract Cost toCost(CostItem costItem);

    public abstract CostItem toCostItem(Cost cost);

    public void validate(@NotNull CostItem costItem, @NotNull BindingResult bindingResult) {
        ValidationUtil.isValid(bindingResult, costItem, (Class<?>[]) null);
    }

    protected void validate(@NotNull CostItem costItem, @NotNull BindingResult bindingResult, Class<?>... classes) {
        ValidationUtil.isValid(bindingResult, costItem, classes);
    }

    public void setCostFields(List<CostField> costFields) {
        this.costFields = costFields.stream().collect(Collectors.toMap(CostField::getTitle, Function.<CostField>identity()));
    }

    public List<Cost> initializeCost() {
        ArrayList<Cost> costs = new ArrayList<>();
        return costs;
    }
}
