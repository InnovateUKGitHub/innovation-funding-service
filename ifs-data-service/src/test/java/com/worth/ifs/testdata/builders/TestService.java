package com.worth.ifs.testdata.builders;

import javax.transaction.Transactional;
import java.util.function.Supplier;

/**
 * TODO DW - document this class
 */
public interface TestService {

    @Transactional
    void doWithinTransaction(Runnable runnable);

    @Transactional
    <T> T doWithinTransaction(Supplier<T> supplier);

    void flushAndClearSession();
}
