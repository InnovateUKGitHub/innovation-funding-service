package org.innovateuk.ifs.util;

import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * This class is a general util class containing method converting or handling some kind of {@link Number} object.
 */
public final class NumberUtils {

    private NumberUtils() {}

    public static BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        value = cleanNumberValue(value);

        if (StringUtils.isEmpty(value))
            return defaultValue != null ? new BigDecimal(defaultValue) : BigDecimal.ZERO;

        NumberFormat nf = getNumberFormat();

        try {
            return new BigDecimal(nf.parse(value).toString());
        } catch (NumberFormatException nfe) {
            throw new BigDecimalNumberFormatException(value);
        } catch (ParseException e) {
            throw new BigDecimalNumberFormatException(value);
        }
    }

    public static Integer getIntegerValue(String inputValue, Integer defaultValue) {
        String value = cleanNumberValue(inputValue);
        if (StringUtils.isEmpty(value))
            return defaultValue != null ? defaultValue : 0;

        NumberFormat nf = getNumberFormat();

        nf.setParseIntegerOnly(true);
        try {
            String stringValue = nf.parse(value).toString();
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException nfe) {
            throw new IntegerNumberFormatException(inputValue);
        } catch (ParseException e) {
            throw new IntegerNumberFormatException(inputValue);
        }
    }

    public static NumberFormat getNumberFormat() {
        return DecimalFormat.getInstance(Locale.UK);
    }

    public static String cleanNumberValue(String value) {
        return value.replace(" ", "").replaceAll("[^\\d.-]", "");
    }
}
