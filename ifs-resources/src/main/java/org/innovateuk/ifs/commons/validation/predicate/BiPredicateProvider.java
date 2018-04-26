package org.innovateuk.ifs.commons.validation.predicate;

import java.util.function.BiPredicate;

/*
 * Interface that provides a predicate defined for use in the @{FieldComparisonValidator}
 */

public interface BiPredicateProvider<T, U> {
    BiPredicate<T, U> predicate();
}
