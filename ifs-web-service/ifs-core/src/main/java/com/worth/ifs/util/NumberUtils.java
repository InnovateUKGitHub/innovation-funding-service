package com.worth.ifs.util;

import com.worth.ifs.exception.BigDecimalNumberFormatException;
import com.worth.ifs.exception.IntegerNumberFormatException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * This class is a general util class containing method converting or handling some kind of {@link Number} object.
 */
public class NumberUtils {
    public static Locale LOCALE_UK = Locale.UK;


    public static BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        value = cleanNumberValue(value);

        if (StringUtils.isEmpty(value))
            return defaultValue != null ? new BigDecimal(defaultValue) : BigDecimal.ZERO;

        NumberFormat nf = getNumberFormat(value);
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

        NumberFormat nf = getNumberFormat(value);

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

    public static BigInteger getBigIntegerValue(String inputValue, BigInteger defaultValue) {
        String value = cleanNumberValue(inputValue);
        if (StringUtils.isEmpty(value))
            return defaultValue != null ? defaultValue : BigInteger.ZERO;


        NumberFormat nf = getNumberFormat(value);

        nf.setParseIntegerOnly(true);
        try {
            String stringValue = nf.parse(value).toString();
            return new BigInteger(stringValue);
        } catch (NumberFormatException nfe) {
            throw new IntegerNumberFormatException(inputValue);
        } catch (ParseException e) {
            throw new IntegerNumberFormatException(inputValue);
        }
    }

    public static NumberFormat getNumberFormat(String value) {
        NumberFormat nf;
        nf = DecimalFormat.getInstance(LOCALE_UK);
        return nf;
    }

    public static String cleanNumberValue(String value) {
        value = value.replace(" ", "");
        value = value.replaceAll("[^\\d.-]", "");
        return value;
    }
}