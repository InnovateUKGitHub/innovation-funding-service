package org.innovateuk.ifs.address.form;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.validation.ValidAddressForm;

import java.util.List;
import java.util.function.Supplier;

@ValidAddressForm
public class AddressForm {
    private String postcodeInput;
    private Integer selectedPostcodeIndex;
    private AddressResource address;
    private List<AddressResource> postcodeResults;
    private boolean searchPostcode;
    private boolean manualAddress;

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

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    public List<AddressResource> getPostcodeResults() {
        return postcodeResults;
    }

    public void setPostcodeResults(List<AddressResource> postcodeResults) {
        this.postcodeResults = postcodeResults;
    }

    public boolean isSearchPostcode() {
        return searchPostcode;
    }

    public void setSearchPostcode(boolean searchPostcode) {
        this.searchPostcode = searchPostcode;
    }

    public boolean isManualAddress() {
        return manualAddress;
    }

    public void setManualAddress(boolean manualAddress) {
        this.manualAddress = manualAddress;
    }

    public AddressResource getSelectedAddress(Supplier<List<AddressResource>> resultsSupplier) {
        if (manualAddress) {
            return address;
        } else {
            return resultsSupplier.get().get(selectedPostcodeIndex);
        }

    }
}
