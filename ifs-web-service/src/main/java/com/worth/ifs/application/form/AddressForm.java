package com.worth.ifs.application.form;

import com.worth.ifs.address.resource.AddressResource;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressForm  implements Serializable {
    private boolean triedToSave = false;
    @NotEmpty
    private String postcodeInput = "";
    private String selectedPostcodeIndex;
    @Valid
    private transient AddressResource selectedPostcode = null;
    @Valid
    private transient List<AddressResource> postcodeOptions;
    private boolean manualAddress = false;

    public AddressForm(String postcodeInput) {
        this.postcodeOptions = new ArrayList<>();
        this.postcodeInput = postcodeInput;
    }

    public AddressForm() {
        this.postcodeOptions = new ArrayList<>();
    }

    public String getPostcodeInput() {
        return postcodeInput;
    }

    public void setPostcodeInput(String postcodeInput) {
        this.postcodeInput = postcodeInput;
    }

    public String getSelectedPostcodeIndex() {
        return selectedPostcodeIndex;
    }

    public void setSelectedPostcodeIndex(String selectedPostcodeIndex) {
        this.selectedPostcodeIndex = selectedPostcodeIndex;
    }

    public AddressResource getSelectedPostcode() {
        return selectedPostcode;
    }

    public void setSelectedPostcode(AddressResource selectedPostcode) {
        this.selectedPostcode = selectedPostcode;
    }

    public List<AddressResource> getPostcodeOptions() {
        return postcodeOptions;
    }

    public void setPostcodeOptions(List<AddressResource> postcodeOptions) {
        this.postcodeOptions = postcodeOptions;
    }

    public boolean isManualAddress() {
        return manualAddress;
    }

    public void setManualAddress(boolean manualAddress) {
        this.manualAddress = manualAddress;
    }

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }
}
