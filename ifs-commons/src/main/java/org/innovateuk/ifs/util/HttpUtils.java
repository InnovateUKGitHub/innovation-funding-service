package org.innovateuk.ifs.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

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

    /**
     * Get a request's query parameters as a {@link MultiValueMap}.
     * May be useful in specific situations where Spring cannot map these appropriately.
     *
     * @param request to extract query string parameters from
     * @return a {@link MultiValueMap} containing the key-value pairs of the query string
     */
    public static MultiValueMap<String, String> getQueryStringParameters(HttpServletRequest request) {
        return new LinkedMultiValueMap<>(
                UriComponentsBuilder.newInstance()
                        .query(request.getQueryString())
                        .build()
                        .getQueryParams()
        );
    }

    /**
     *First off filters out the parameters which we are interested in but they are still of the form e.g. (  key -> formInput[12].MMYYYY.year,
     *                                                                                                       value -> 7
     *                                                                                                       key -> formInput[12].MMYYYY.month,
     *                                                                                                       value -> 2011)
     *Then it makes a map of maps keyed of the form input e.g.
     *And eventually it returns a Map with in the key the name of the input (formInput[12]) and combined value in the value (7-2011)
     * @param parameterMap
     * @return
     */
    public static Map<String, String> processForMMYYYY(Map<String, String> parameterMap) {
        Map<String, String> parametersThatNeedProcessing = simpleFilter(parameterMap, key -> key.endsWith(MM_YYYY_MONTH_APPEND) || key.endsWith(MM_YYYY_YEAR_APPEND));
        Map<String, Map<String, String>> parametersThatNeedProcessingGroupedByFormInput = simpleGroupBy(parametersThatNeedProcessing, key -> key.replace(MM_YYYY_MONTH_APPEND, "").replace(MM_YYYY_YEAR_APPEND, ""));
        Map<String, String> processed = simpleMapEntry(parametersThatNeedProcessingGroupedByFormInput, Entry::getKey,
                entry -> {
                    Map<String, String> components = entry.getValue();
                    String key = entry.getKey();
                    String month = components.get(key + MM_YYYY_MONTH_APPEND);
                    if (month != null) {
                        month = "00".substring(Math.min(month.length(), 2)) + month;
                    } else {
                        month = "";
                    }
                    String year = components.get(key + MM_YYYY_YEAR_APPEND);
                    if (year != null) {
                        year = "0000".substring(Math.min(year.length(), 4)) + year;
                    } else {
                        year = "";
                    }
                    String value = month + "-" + year;
                    return value;
                });

        return processed;
    }


    /**
     * Parse a {@link HttpServletRequest} to retrieve the full URL string (with query parameters).
     *
     * This can be useful as only the {@link HttpServletRequest#getRequestURL}
     * and {@link HttpServletRequest#getQueryString} methods are exposed
     * (which are not always sufficient).
     *
     * @param request from a controller method
     * @return the full URL string
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
