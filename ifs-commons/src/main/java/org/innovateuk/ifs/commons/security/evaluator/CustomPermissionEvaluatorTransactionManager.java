package org.innovateuk.ifs.commons.security.evaluator;

import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.function.Supplier;

/**
 * A transaction manager that the {@link AbstractCustomPermissionEvaluator} uses to wrap all permission checks in a single transaction
 * in order to better make use of Hibernate's Session cache
 */
public interface CustomPermissionEvaluatorTransactionManager {

    @NotSecured(value = "This method is called during the permission checking process", mustBeSecuredByOtherServices = false)
    void doWithinTransaction(Runnable runnable);

    @NotSecured(value = "This method is called during the permission checking process", mustBeSecuredByOtherServices = false)
    <T> T doWithinTransaction(Supplier<T> supplier);
}
