package org.innovateuk.ifs.commons.security.evaluator;

import java.util.function.Supplier;

/**
 * A transaction manager that the {@link CustomPermissionEvaluator} uses to wrap all permission checks in a single transaction
 * in order to better make use of Hibernate's Session cache
 */
public interface CustomPermissionEvaluatorTransactionManager {

    void doWithinTransaction(Runnable runnable);

    <T> T doWithinTransaction(Supplier<T> supplier);
}
