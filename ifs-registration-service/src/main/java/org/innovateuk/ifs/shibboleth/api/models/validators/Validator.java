package org.innovateuk.ifs.shibboleth.api.models.validators;

import org.innovateuk.ifs.shibboleth.api.exceptions.RestResponseException;

public interface Validator<A, B extends RestResponseException> {

    void validate(A value) throws B;

}