package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTransactionManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Supplier;

/**
 * TODO DW - document this class
 */
@Service
@Transactional
public class TransactionalCustomPermissionEvaluatorTransactionManager implements CustomPermissionEvaluatorTransactionManager {

    @Override
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
