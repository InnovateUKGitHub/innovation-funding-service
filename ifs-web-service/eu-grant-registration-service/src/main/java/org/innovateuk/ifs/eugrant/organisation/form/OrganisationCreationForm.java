package org.innovateuk.ifs.eugrant.organisation.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is used for the company house form, while creating a new application.
 */
public class OrganisationCreationForm implements Serializable {
    @Valid
    private AddressForm addressForm = new AddressForm();
    private boolean triedToSave = false;

    @NotNull(message = "{validation.standard.organisationtype.required}")
    private EuOrganisationType organisationType;

    @NotBlank(message = "{validation.standard.organisationsearchname.required}")
    // on empty value don't check pattern since then there already is a validation message.
    private String organisationSearchName;
    private String searchOrganisationId;
    private boolean organisationSearching;
    private boolean manualEntry = false;
    private boolean useSearchResultAddress = false;
    private transient List<OrganisationSearchResult> organisationSearchResults;
    @NotBlank(message = "{validation.standard.organisationname.required}")
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

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
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

    //Fields to be ignored in cookie.
    @JsonIgnore
    public List<OrganisationSearchResult> getOrganisationSearchResults() {
        return organisationSearchResults;
    }

    @JsonIgnore
    public boolean isResearch(){
        return organisationType != null && organisationType == EuOrganisationType.RESEARCH;
    }
}
