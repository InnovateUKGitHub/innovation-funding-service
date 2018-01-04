package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.error.Error;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a class that can, given an original set of File Upload errors from the data layer, translate them into
 * appropriate user-facing errors for use in the front end
 */
public interface FileUploadErrorTranslator {

    /**
     * Given a List of Errors, this method will translate those Errors into user-facing errors appropriate to the working
     * context of the User.
     *
     * @param fieldIdFn - a function to derive the correct target fieldId per Error being transalated
     * @param originalErrors - a list of the original Errors from the data layer
     * @return a translated set of field errors, with field ids generated from fieldIdFn
     */
    List<Error> translateFileUploadErrors(Function<Error, String> fieldIdFn, List<Error> originalErrors);
}
