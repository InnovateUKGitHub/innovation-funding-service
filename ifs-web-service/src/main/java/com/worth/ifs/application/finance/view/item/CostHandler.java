package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CostHandler {
    protected final Log log = LogFactory.getLog(getClass());

    Map<String, CostField> costFields = new HashMap<>();

    public abstract CostItem toCostItem(Long id, List<CostFormField> costFormFields);

    public CostHandler() {
    }

    protected BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException nfe) {
            return new BigDecimal(defaultValue);
        }
    }

    protected Integer getIntegerValue(String value, Integer defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
