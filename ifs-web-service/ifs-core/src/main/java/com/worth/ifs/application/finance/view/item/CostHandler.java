package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * CostHandlers are used to convert form fields to costItems
 */
public abstract class CostHandler {
    public static final Locale LOCALE_UK = Locale.UK;
    protected Log LOG = LogFactory.getLog(this.getClass());
    Map<String, CostField> costFields = new HashMap<>();

    public abstract CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields);

    public CostHandler() {
    }

    public BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        value = cleanNumberValue(value);

        if(StringUtils.isEmpty(value))
            return new BigDecimal(defaultValue);

        NumberFormat nf = getNumberFormat(value);
        try {
            return new BigDecimal(nf.parse(value).toString());
        } catch (NumberFormatException nfe) {
            throw nfe;
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
    }

    public Integer getIntegerValue(String inputValue, Integer defaultValue) {
        String value = cleanNumberValue(inputValue);
        if(StringUtils.isEmpty(value))
            return defaultValue;


        NumberFormat nf = getNumberFormat(value);

        nf.setParseIntegerOnly(true);
        try {
            String stringValue = nf.parse(value).toString();
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(inputValue);
        } catch (ParseException e) {
            throw new NumberFormatException(inputValue);
        }
    }

    private NumberFormat getNumberFormat(String value) {
        NumberFormat nf;
        nf = DecimalFormat.getInstance(LOCALE_UK);
        return nf;
    }

    private String cleanNumberValue(String value) {
        value = value.replace(" ", "");
        value = value.replaceAll("[^\\d.-]", "");
        return value;
    }
}
