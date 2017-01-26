package org.innovateuk.ifs.util;

import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.MapFunctions.simplePartition;

/**
 * A set of utilities for performing commonly used functions related to HTTP.
 *
 * Created by dwatson on 01/10/15.
 */
public final class HttpUtils {

    public static final String MM_YYYY_MONTH_APPEND = ".MMYYYY.month";
    public static final String MM_YYYY_YEAR_APPEND = ".MMYYYY.year";

    private HttpUtils() {
    }

    /**
     * Checks to see whether or not the given request parameter is present and if so, returns a non-empty Optional upon which
     * an "ifPresent" call can be inChain
     *
     * @param parameterName
     * @param request
     * @return
     */
    public static Optional<String> requestParameterPresent(String parameterName, HttpServletRequest request) {
        Map<String, String> params = simpleMapValue(request.getParameterMap(), array -> array != null && array.length > 0 ? array[0] : null);
        // See if we have any default matching.
        String defaultMatch = params.get(parameterName);
        if (defaultMatch != null) {
            return ofNullable(defaultMatch);
        }
        // Any that are matching for our MMYYYY dates.
        String matchForMMYYYY = processForMMYYYY(params).get(parameterName);
        if (matchForMMYYYY != null) {
            return ofNullable(matchForMMYYYY);
        }
        return Optional.empty();
    }

    static Map<String, String> processForMMYYYY(Map<String, String> parameterMap) {
        Map<String, String> parametersThatNeedProcessing = simpleFilter(parameterMap, key -> key.endsWith(MM_YYYY_MONTH_APPEND) || key.endsWith(MM_YYYY_YEAR_APPEND));
        // We should now only have the parameters which we are interested in but they are still of the form e.g.
        // key ->
        //   formInput[12].MMYYYY.year,
        // value ->
        //   7
        // key ->
        //   formInput[12].MMYYYY.month,
        // value -> 2011
        Map<String, Map<String, String>> parametersThatNeedProcessingGroupedByFormInput = simpleGroupBy(parametersThatNeedProcessing, key -> key.replace(MM_YYYY_MONTH_APPEND, "").replace(MM_YYYY_YEAR_APPEND, ""));
        // We now have a map of maps keyed of the form input e.g.
        // key ->
        //    formInput[12]
        // value ->
        //    map -> key ->
        //             formInput[12].MMYYYY.year
        //           value ->
        //              2011
        //           key ->
        //             formInput[12].MMYYYY.month
        //           value -> 7
        Map<String, String> processed = simpleMapEntry(parametersThatNeedProcessingGroupedByFormInput, Entry::getKey,
                entry -> {
                    Map<String, String> components = entry.getValue();
                    String key = entry.getKey();
                    String month = components.get(key + MM_YYYY_MONTH_APPEND);
                    String year = components.get(key + MM_YYYY_YEAR_APPEND);
                    String value = (month != null ? month : "") + "-" + (year != null ? year : "");
                    return value;
                });
        // At this point we should have e.g.
        // key ->
        //   formInput[12]
        // value ->
        //   7-2011
        return processed;
    }


}
