package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * This component is used to wrap a transaction boundary around all permission checks so that the Hibernate second-level cache
 * can better utilised during permission checking to prevent the same entities from being looked up multiple times if multiple rules
 * need be checked
 */
@Service
public class TransactionalCustomPermissionEvaluatorTransactionManager implements CustomPermissionEvaluatorTransactionManager {

    @Override
    @Transactional(readOnly = true)
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
