package org.innovateuk.ifs.util;

/**
 * @deprecated barely used and Optional seems a better choice
 */
@Deprecated
public final class ExceptionFunctions {

    private ExceptionFunctions() {}

    public static <T> T getOrRethrow(ExceptionThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
