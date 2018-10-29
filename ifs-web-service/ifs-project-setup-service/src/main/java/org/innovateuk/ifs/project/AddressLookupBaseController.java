package org.innovateuk.ifs.project;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller handles address lookups
 */
//TODO this is a candidate for refactor

public class AddressLookupBaseController {
    public static final String FORM_ATTR_NAME = "form";
    protected static final String MANUAL_ADDRESS = "manual-address";
    protected static final String SEARCH_ADDRESS = "search-address";
    protected static final String SELECT_ADDRESS = "select-address";

    @Autowired
    private AddressRestService addressRestService;

    protected List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }

    protected FieldError createPostcodeSearchFieldError() {
        return new FieldError("form", "addressForm.postcodeInput", "", true, new String[] {"EMPTY_POSTCODE_SEARCH"}, new Object[] {}, null);
    }
}
