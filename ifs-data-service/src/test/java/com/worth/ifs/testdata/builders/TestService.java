package com.worth.ifs.testdata.builders;

import javax.transaction.Transactional;
import java.util.function.Supplier;

/**
 * A helper service to open up transaction boundaries for the purposes of code that relies on a single transaction in
 * order to work
 */
public interface TestService {

    @Transactional
    void doWithinTransaction(Runnable runnable);

    @Transactional
    <T> T doWithinTransaction(Supplier<T> supplier);

    void flushAndClearSession();
}
