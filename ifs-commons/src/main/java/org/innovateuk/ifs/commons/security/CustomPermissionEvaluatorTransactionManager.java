package org.innovateuk.ifs.commons.security;

import java.util.function.Supplier;

/**
 * TODO DW - document this class
 */
public interface CustomPermissionEvaluatorTransactionManager {

    void doWithinTransaction(Runnable runnable);

    <T> T doWithinTransaction(Supplier<T> supplier);
}
