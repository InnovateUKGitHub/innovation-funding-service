package com.worth.ifs.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A set of utilities for performing commonly used functions related to HTTP.
 *
 * Created by dwatson on 01/10/15.
 */
public class IfsHttpUtils {

    /**
     * Checks to see whether or not the given request parameter is present and if so, returns a non-empty Optional upon which
     * an "ifPresent" call can be inChain
     *
     * @param parameterName
     * @param request
     * @return
     */
    public static Optional<Boolean> requestParameterPresent(String parameterName, HttpServletRequest request) {
        List<String> parameterNames = Collections.list(request.getParameterNames());
        if (parameterNames.stream().anyMatch(name -> name.equals(parameterName))) {
            return Optional.of(true);
        }

        return Optional.empty();
    }
}
