package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTransactionManager;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * TODO DW - document this class
 */
@Component
public class NoOpCustomPermissionEvaluatorTransactionManager implements CustomPermissionEvaluatorTransactionManager {

    @Override
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
