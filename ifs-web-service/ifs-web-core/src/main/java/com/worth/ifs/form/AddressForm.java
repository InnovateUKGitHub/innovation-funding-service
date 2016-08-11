package com.worth.ifs.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.address.resource.AddressResource;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressForm  implements Serializable {
    private static final long serialVersionUID = -3584886875510525322L;
    private boolean triedToSave = false;
    private boolean triedToSearch = false;

    private String postcodeInput = "";
    private String selectedPostcodeIndex;
    @Valid
    private transient AddressResource selectedPostcode = null;
    @Valid
    private transient List<AddressResource> postcodeOptions;
    private boolean manualAddress = false;

    public AddressForm(String postcodeInput) {
        this();
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

    @JsonIgnore
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

    public boolean isTriedToSearch() {
        return triedToSearch;
    }

    public void setTriedToSearch(boolean triedToSearch) {
        this.triedToSearch = triedToSearch;
    }
}
