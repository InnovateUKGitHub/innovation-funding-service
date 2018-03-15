package org.innovateuk.ifs.commons.validation.predicate;

import java.util.function.BiPredicate;

public interface BiPredicateProvider<T, U> {
    BiPredicate<T, U> predicate();
}
