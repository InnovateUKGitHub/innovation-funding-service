package com.worth.ifs.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * A set of utilities for performing commonly used functions related to HTTP.
 *
 * Created by dwatson on 01/10/15.
 */
public final class HttpUtils {

	private HttpUtils() {}
	
    /**
     * Checks to see whether or not the given request parameter is present and if so, returns a non-empty Optional upon which
     * an "ifPresent" call can be inChain
     *
     * @param parameterName
     * @param request
     * @return
     */
    public static Optional<String> requestParameterPresent(String parameterName, HttpServletRequest request) {
        List<String> parameterNames = Collections.list(request.getParameterNames());
        List<String> matchingParameter = parameterNames.stream().filter(name -> name.equals(parameterName)).collect(toList());
        if (!matchingParameter.isEmpty()) {
            return Optional.ofNullable(request.getParameter(matchingParameter.get(0)));
        }

        return Optional.empty();
    }
}
