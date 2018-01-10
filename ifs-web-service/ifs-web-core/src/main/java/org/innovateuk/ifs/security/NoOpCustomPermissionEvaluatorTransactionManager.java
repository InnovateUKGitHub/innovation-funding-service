package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTransactionManager;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * A no-op transaction manager for use in the web layer.  To make the {@link CustomPermissionEvaluator}
 * portable between the data and web layer, this no-op component takes the place of the transactional component as used in the data layer
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
