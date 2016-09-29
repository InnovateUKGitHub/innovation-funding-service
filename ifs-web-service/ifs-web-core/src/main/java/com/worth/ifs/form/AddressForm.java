package com.worth.ifs.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.address.resource.AddressResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddressForm that = (AddressForm) o;

        return new EqualsBuilder()
                .append(triedToSave, that.triedToSave)
                .append(triedToSearch, that.triedToSearch)
                .append(manualAddress, that.manualAddress)
                .append(postcodeInput, that.postcodeInput)
                .append(selectedPostcodeIndex, that.selectedPostcodeIndex)
                .append(selectedPostcode, that.selectedPostcode)
                .append(postcodeOptions, that.postcodeOptions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(triedToSave)
                .append(triedToSearch)
                .append(postcodeInput)
                .append(selectedPostcodeIndex)
                .append(selectedPostcode)
                .append(postcodeOptions)
                .append(manualAddress)
                .toHashCode();
    }
}
