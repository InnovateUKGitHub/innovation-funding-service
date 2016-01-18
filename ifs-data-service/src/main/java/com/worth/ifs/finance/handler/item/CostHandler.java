package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CostHandler {
    Map<String, CostField> costFields = new HashMap<>();

    public abstract Cost toCost(CostItem costItem);
    public abstract CostItem toCostItem(Cost cost);

    public CostHandler() {
    }

    public CostHandler(List<CostField> costFields) {
        this.costFields = costFields.stream().collect(Collectors.toMap(CostField::getTitle, Function.<CostField>identity()));
    }
}
