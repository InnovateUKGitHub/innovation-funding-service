package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CostHandlers are used to convert form fields to costItems
 */
public abstract class CostHandler {
    protected final Log log = LogFactory.getLog(getClass());

    Map<String, CostField> costFields = new HashMap<>();

    public abstract CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields);

    public CostHandler() {
    }

    public BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        NumberFormat nf = DecimalFormat.getInstance(Locale.UK);
        try {
            return new BigDecimal(nf.parse(value).toString());
        } catch (NumberFormatException nfe) {
            return new BigDecimal(defaultValue);
        } catch (ParseException e) {
            return new BigDecimal(defaultValue);
        }
    }

    public Integer getIntegerValue(String value, Integer defaultValue) {
        NumberFormat nf = DecimalFormat.getInstance(Locale.UK);
        nf.setParseIntegerOnly(true);
        try {
            return Integer.valueOf(nf.parse(value).toString());
        } catch (NumberFormatException nfe) {
            return defaultValue;
        } catch (ParseException e) {
            return defaultValue;
        }
    }
}
