package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.bankdetails.form.ProjectDetailsAddressForm;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DETAILS_ADDRESS_SEARCH_OR_TYPE_MANUALLY;
import static com.worth.ifs.commons.error.Error.fieldError;

public class AddressLookupBaseController {
    public static final String FORM_ATTR_NAME = "form";
    static final String MANUAL_ADDRESS = "manual-address";
    static final String SEARCH_ADDRESS = "search-address";
    static final String SELECT_ADDRESS = "select-address";

    @Autowired
    private AddressRestService addressRestService;

    void processAddressLookupFields(ProjectDetailsAddressForm form) {
        addAddressOptions(form);
        addSelectedAddress(form);
    }

    Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final OrganisationAddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> OrganisationAddressType.valueOf(a.getAddressType().getName()).equals(addressType)).findFirst();
    }

    OrganisationAddressResource getOrganisationAddressResourceOrNull(
            ProjectDetailsAddressForm form,
            OrganisationResource organisationResource,
            OrganisationAddressType addressTypeToUseForNewAddress){
        OrganisationAddressResource organisationAddressResource = null;
        if(existingAddressSelected(form.getAddressType())){
            Optional<OrganisationAddressResource> organisationAddress = getAddress(organisationResource, form.getAddressType());
            if (organisationAddress.isPresent()) {
                organisationAddressResource = organisationAddress.get();
            }
        } else {
            form.getAddressForm().setTriedToSave(true);
            AddressResource newAddressResource = form.getAddressForm().getSelectedPostcode();
            organisationAddressResource = new OrganisationAddressResource(organisationResource, newAddressResource, new AddressTypeResource((long)addressTypeToUseForNewAddress.ordinal(), addressTypeToUseForNewAddress.name()));
        }
        return organisationAddressResource;
    }

    boolean hasNonAddressErrors(BindingResult bindingResult){
        return bindingResult.getFieldErrors().stream().filter(e -> (!e.getField().contains("addressForm"))).count() > 0;
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(ProjectDetailsAddressForm projectDetailsAddressViewModelForm) {
        if (StringUtils.hasText(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = projectDetailsAddressViewModelForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput());
        }
    }

    /**
     * if user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    private void addSelectedAddress(ProjectDetailsAddressForm projectDetailsAddressViewModelForm) {
        AddressForm addressForm = projectDetailsAddressViewModelForm.getAddressForm();
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }

    private boolean existingAddressSelected(OrganisationAddressType organisationAddressType){
        return organisationAddressType != null && organisationAddressType != ADD_NEW;
    }

    FieldError createPostcodeSearchFieldError() {
        return new FieldError("form", "addressForm.postcodeInput", "", true, new String[] {"EMPTY_POSTCODE_SEARCH"}, new Object[] {}, null);
    }

    void addAddressNotProvidedValidationError(BindingResult bindingResult, ValidationHandler validationHandler){
        ValidationMessages validationMessages = new ValidationMessages(bindingResult);
        validationMessages.addError(fieldError("addressType", new Error(PROJECT_SETUP_PROJECT_DETAILS_ADDRESS_SEARCH_OR_TYPE_MANUALLY)));
        validationHandler.addAnyErrors(validationMessages);
    }
}
