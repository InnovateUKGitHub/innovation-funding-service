package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.function.Supplier;

/**
 * A helper service to open up transaction boundaries for the purposes of code that relies on a single transaction in
 * order to work
 */
public interface TestService {

    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    void doWithinTransaction(Runnable runnable);

    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    <T> T doWithinTransaction(Supplier<T> supplier);

    @NotSecured(value = "Test service only", mustBeSecuredByOtherServices = false)
    void flushAndClearSession();
}
