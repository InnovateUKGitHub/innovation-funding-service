package org.innovateuk.ifs.util;


import org.innovateuk.ifs.commons.service.ExceptionThrowingSupplier;

public class ExceptionFunctions {

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
