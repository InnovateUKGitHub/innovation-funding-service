package com.worth.ifs.testdata.builders;

import com.worth.ifs.commons.security.NotSecured;

import javax.transaction.Transactional;
import java.util.function.Supplier;

/**
 * A helper service to open up transaction boundaries for the purposes of code that relies on a single transaction in
 * order to work
 */
public interface TestService {

    @Transactional
    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    void doWithinTransaction(Runnable runnable);

    @Transactional
    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    <T> T doWithinTransaction(Supplier<T> supplier);

    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    void flushAndClearSession();
}
