package org.innovateuk.ifs.project.bankdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.address.form.AddressForm;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;

public class ChangeBankDetailsForm {

    @Size.List ({
            @Size(min=8, message="{validation.standard.registrationnumber.format}"),
            @Size(max=8, message="{validation.standard.registrationnumber.format}"),
    })
    private String registrationNumber;

    @NotBlank(message = "{validation.standard.organisationname.required}")
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String organisationName;

    @Pattern(regexp = "\\d{6}", message = "{validation.standard.sortcode.format}")
    private String sortCode;

    @Pattern(regexp = "\\d{8}", message = "{validation.standard.accountnumber.format}")
    private String accountNumber;

    @Valid
    private AddressForm addressForm = new AddressForm();

    private final OrganisationAddressType addressType = BANK_DETAILS;

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public ChangeBankDetailsForm() {
    }

    public ChangeBankDetailsForm(String registrationNumber, String organisationName) {
        this.registrationNumber = registrationNumber;
        this.organisationName = organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChangeBankDetailsForm that = (ChangeBankDetailsForm) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(registrationNumber, that.registrationNumber)
                .append(organisationName, that.organisationName)
                .append(sortCode, that.sortCode)
                .append(accountNumber, that.accountNumber)
                .append(addressForm, that.addressForm)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(registrationNumber)
                .append(organisationName)
                .append(sortCode)
                .append(accountNumber)
                .append(addressForm)
                .toHashCode();
    }

    public OrganisationAddressType getAddressType() {
        return addressType;
    }
}
