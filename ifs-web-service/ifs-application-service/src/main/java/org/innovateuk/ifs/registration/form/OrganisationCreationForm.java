package org.innovateuk.ifs.registration.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class OrganisationCreationForm implements Serializable {
    @Valid
    private AddressForm addressForm = new AddressForm();
    private boolean triedToSave = false;

    @NotNull(message = "{validation.standard.organisationtype.required}")
    private Long organisationTypeId;

    private OrganisationTypeEnum organisationTypeEnum;
    @NotEmpty(message = "{validation.standard.organisationsearchname.required}")
    // on empty value don't check pattern since then there already is a validation message.
    private String organisationSearchName;
    private String searchOrganisationId;
    private boolean organisationSearching;
    private boolean manualEntry = false;
    private boolean useSearchResultAddress = false;
    private transient List<OrganisationSearchResult> organisationSearchResults;
    @NotEmpty(message = "{validation.standard.organisationname.required}")
    private String organisationName;

    public OrganisationCreationForm() {
        this.organisationSearchResults = new ArrayList<>();
    }

    public OrganisationCreationForm(List<OrganisationSearchResult> companyHouseList) {
        this.organisationSearchResults = companyHouseList;
    }

    public boolean isManualEntry() {
        return manualEntry;
    }

    public void setManualEntry(boolean manualEntry) {
        this.manualEntry = manualEntry;
    }

    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public void setOrganisationTypeId(Long organisationTypeId) {
        this.organisationTypeId = organisationTypeId;
    }

    @JsonIgnore
    public boolean isResearch(){
        if(organisationTypeId != null) {
            return OrganisationTypeEnum.getFromId(organisationTypeId).equals(OrganisationTypeEnum.RESEARCH);
        }
        else {
            return false;
        }
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationSearchName() {
        return organisationSearchName;
    }

    public void setOrganisationSearchName(String organisationSearchName) {
        this.organisationSearchName = organisationSearchName;
    }

    public boolean isOrganisationSearching() {
        return organisationSearching;
    }

    public void setOrganisationSearching(boolean organisationSearching) {
        this.organisationSearching = organisationSearching;
    }

    @JsonIgnore
    public List<OrganisationSearchResult> getOrganisationSearchResults() {
        return organisationSearchResults;
    }

    public void setOrganisationSearchResults(List<OrganisationSearchResult> organisationSearchResults) {
        this.organisationSearchResults = organisationSearchResults;
    }

    public String getSearchOrganisationId() {
        return searchOrganisationId;
    }

    public void setSearchOrganisationId(String searchOrganisationId) {
        this.searchOrganisationId = searchOrganisationId;
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }

    public boolean isUseSearchResultAddress() {
        return useSearchResultAddress;
    }

    public void setUseSearchResultAddress(boolean useSearchResultAddress) {
        this.useSearchResultAddress = useSearchResultAddress;
    }

    @JsonIgnore
    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return OrganisationTypeEnum.getFromId(organisationTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationCreationForm that = (OrganisationCreationForm) o;

        return new EqualsBuilder()
                .append(triedToSave, that.triedToSave)
                .append(organisationSearching, that.organisationSearching)
                .append(manualEntry, that.manualEntry)
                .append(useSearchResultAddress, that.useSearchResultAddress)
                .append(addressForm, that.addressForm)
                .append(organisationTypeId, that.organisationTypeId)
                .append(organisationTypeEnum, that.organisationTypeEnum)
                .append(organisationSearchName, that.organisationSearchName)
                .append(searchOrganisationId, that.searchOrganisationId)
                .append(organisationSearchResults, that.organisationSearchResults)
                .append(organisationName, that.organisationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(addressForm)
                .append(triedToSave)
                .append(organisationTypeId)
                .append(organisationTypeEnum)
                .append(organisationSearchName)
                .append(searchOrganisationId)
                .append(organisationSearching)
                .append(manualEntry)
                .append(useSearchResultAddress)
                .append(organisationSearchResults)
                .append(organisationName)
                .toHashCode();
    }
}
