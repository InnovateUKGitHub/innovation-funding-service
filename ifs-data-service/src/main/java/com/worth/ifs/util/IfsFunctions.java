package com.worth.ifs.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by dwatson on 01/10/15.
 */
public class IfsFunctions {

    /**
     * Checks to see whether or not the given request parameter is present and if so, returns a non-empty Optional upon which
     * an "ifPresent" call can be chained
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


    /**
     * A class to allow chaining of an "else" function on unsuccessful "ifPresent"
     *
     * @param <T>
     */
    public static class IfPresentElse<T> {

        private final boolean wasPresent;
        private final T wasPresentResult;

        public IfPresentElse(boolean wasPresent, T wasPresentResult) {
            this.wasPresent = wasPresent;
            this.wasPresentResult = wasPresentResult;
        }

        public T orElse(Supplier<T> elseFunction) {
            if (wasPresent) {
                return wasPresentResult;
            }
            return elseFunction.get();
        }
    }


    /**
     * An ifPresent method that allows return values, unlike Optional's ifPresent.  Will return an IfPresentElse which a "orElse"
     * call can be supplied to in the event that the ifPresent test is false
     *
     * @param optional
     * @param ifPresentFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> IfPresentElse<T> ifPresent(Optional<R> optional, Function<R, T> ifPresentFunction) {

        if (optional.isPresent()) {
            T result = ifPresentFunction.apply(optional.get());
            return new IfPresentElse<>(true, result);
        }

        return new IfPresentElse(false, null);
    }
}
