package org.innovateuk.ifs.address.form;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.validation.ValidAddressForm;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;

@ValidAddressForm
public class AddressForm {
    public static final String FORM_ACTION_PARAMETER = "addressForm.action";

    public enum Action {
        SEARCH_POSTCODE,
        CHANGE_POSTCODE,
        ENTER_MANUAL,
        SAVE
    }

    public enum AddressType {
        POSTCODE_LOOKUP,
        MANUAL_ENTRY
    }

    private Action action = Action.SAVE;
    private AddressType addressType;
    private String postcodeInput;
    private Integer selectedPostcodeIndex;
    private AddressResource manualAddress;
    private List<AddressResource> postcodeResults;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getPostcodeInput() {
        return postcodeInput;
    }

    public void setPostcodeInput(String postcodeInput) {
        this.postcodeInput = postcodeInput;
    }

    public Integer getSelectedPostcodeIndex() {
        return selectedPostcodeIndex;
    }

    public void setSelectedPostcodeIndex(Integer selectedPostcodeIndex) {
        this.selectedPostcodeIndex = selectedPostcodeIndex;
    }

    public AddressResource getManualAddress() {
        return manualAddress;
    }

    public void setManualAddress(AddressResource manualAddress) {
        this.manualAddress = manualAddress;
    }

    public List<AddressResource> getPostcodeResults() {
        return postcodeResults;
    }

    public void setPostcodeResults(List<AddressResource> postcodeResults) {
        this.postcodeResults = postcodeResults;
    }

    public AddressResource getSelectedAddress(Function<String, List<AddressResource>> resultsSupplier) {
        if (AddressType.MANUAL_ENTRY.equals(addressType)) {
            return manualAddress;
        } else if (AddressType.POSTCODE_LOOKUP.equals(addressType)) {
            return resultsSupplier.apply(postcodeInput).get(selectedPostcodeIndex);
        } else {
            throw new RuntimeException("Address type not selected");
        }
    }

    public void handleAction(Function<String, List<AddressResource>> resultsSupplier) {
        if (action == Action.ENTER_MANUAL) {
            addressType = AddressType.MANUAL_ENTRY;
        } else if (action == Action.SEARCH_POSTCODE) {
            addressType = AddressType.POSTCODE_LOOKUP;
            postcodeResults = resultsSupplier.apply(postcodeInput);
        } else if (action == Action.CHANGE_POSTCODE) {
            addressType = null;
        }
    }

    public boolean isManualAddressEntry() {
        return addressType == AddressType.MANUAL_ENTRY;
    }

    public boolean isPostcodeAddressEntry() {
        return addressType == AddressType.POSTCODE_LOOKUP;
    }


    public void validateAction(BindingResult bindingResult) {
        if (action == Action.SEARCH_POSTCODE) {
            if (isNullOrEmpty(postcodeInput)) {
                bindingResult.rejectValue("addressForm.postcodeInput", "validation.field.must.not.be.blank");
            }
        }
    }



}
