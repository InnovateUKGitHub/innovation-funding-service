package org.innovateuk.ifs.registration.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Object to store the data that is used for the companies house form, while creating a new application.
 */

@FieldRequiredIf(required = "organisationSearchName", argument = "improvedSearchDisabled", predicate = true, message = "{validation.standard.organisationsearchname.required}")
@FieldRequiredIf(required = "organisationSearchName", argument = "improvedSearchEnabled", predicate = true, message = "{validation.improved.organisationsearchname.required}")
@FieldRequiredIf(required = "organisationName", argument = "manualEntry", predicate = true, message = "{validation.manualentry.organisationname.required}")
public class OrganisationCreationForm implements Serializable {
    private boolean triedToSave = false;

    @NotNull(message = "{validation.standard.organisationtype.required}")
    private Long organisationTypeId;
    private String organisationSearchName;
    private String searchOrganisationId;
    private boolean organisationSearching;
    private boolean manualEntry = false;
    private transient List<OrganisationSearchResult> organisationSearchResults;
    private String organisationName;
    private Boolean newOrganisationSearchEnabled;
    private Long selectedExistingOrganisationId;
    private String selectedExistingOrganisationName;
    private int searchPageIndexPosition = 1;
    private int totalSearchResults = 0;
    private LocalDate dateOfIncorporation;
    private List<OrganisationSicCodeResource> sicCodes = new ArrayList<>();
    private List<OrganisationExecutiveOfficerResource> executiveOfficers = new ArrayList<>();
    private AddressResource organisationAddress;
    private String organisationNumber;
    private String businessType;

    @Valid
    private AddressForm addressForm;


    public OrganisationCreationForm() {
        this.organisationSearchResults = new ArrayList<>();
        this.sicCodes = new ArrayList<>();
        this.sicCodes.add(new OrganisationSicCodeResource());
        this.executiveOfficers = new ArrayList<>();
        executiveOfficers.add(new OrganisationExecutiveOfficerResource());

    }

    public OrganisationCreationForm(List<OrganisationSearchResult> companiesHouseList) {
        this.organisationSearchResults = companiesHouseList;
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

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }

    public String getOrganisationNumber() {
        return organisationNumber;
    }

    public void setOrganisationNumber(String organisationNumber) {
        this.organisationNumber = organisationNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }


    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    @JsonIgnore
    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return OrganisationTypeEnum.getFromId(organisationTypeId);
    }

    public int getSearchPageIndexPosition() {
        return searchPageIndexPosition;
    }

    public void setSearchPageIndexPosition(int searchPageIndexPosition) {
        this.searchPageIndexPosition = searchPageIndexPosition;
    }
    public int  getTotalSearchResults() {
        return  totalSearchResults;
    }

    public void setTotalSearchResults(int totalSearchResults) {
        this.totalSearchResults = totalSearchResults;
    }

    public void setNewOrganisationSearchEnabled(boolean newOrganisationSearchEnabled) {
        this.newOrganisationSearchEnabled = newOrganisationSearchEnabled;
    }

    public boolean isNewOrganisationSearchEnabled() {
        return newOrganisationSearchEnabled;
    }

    public Boolean getNewOrganisationSearchEnabled() {
        return newOrganisationSearchEnabled;
    }

    public void setNewOrganisationSearchEnabled(Boolean newOrganisationSearchEnabled) {
        this.newOrganisationSearchEnabled = newOrganisationSearchEnabled;
    }

    public Long getSelectedExistingOrganisationId() {
        return selectedExistingOrganisationId;
    }

    public void setSelectedExistingOrganisationId(Long selectedExistingOrganisationId) {
        this.selectedExistingOrganisationId = selectedExistingOrganisationId;
    }

    public String getSelectedExistingOrganisationName() {
        return selectedExistingOrganisationName;
    }

    public void setSelectedExistingOrganisationName(String selectedExistingOrganisationName) {
        this.selectedExistingOrganisationName = selectedExistingOrganisationName;
    }

    @JsonIgnore
    public boolean isImprovedSearchDisabled() {
        return isOrganisationSearching() && !isNewOrganisationSearchEnabled();
    }

    @JsonIgnore
    public boolean isImprovedSearchEnabled() {
        return isOrganisationSearching() && isNewOrganisationSearchEnabled();
    }

    public LocalDate getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(LocalDate dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }
    public List<OrganisationSicCodeResource> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<OrganisationSicCodeResource> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public List<OrganisationExecutiveOfficerResource> getExecutiveOfficers() {
        return executiveOfficers;
    }

    public void setExecutiveOfficers(List<OrganisationExecutiveOfficerResource> executiveOfficers) {
        this.executiveOfficers = executiveOfficers;
    }
    public AddressResource getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(AddressResource organisationAddress) {
        this.organisationAddress = organisationAddress;
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
                .append(organisationTypeId, that.organisationTypeId)
                .append(organisationSearchName, that.organisationSearchName)
                .append(searchOrganisationId, that.searchOrganisationId)
                .append(organisationSearchResults, that.organisationSearchResults)
                .append(organisationName, that.organisationName)
                .append(searchPageIndexPosition, that.searchPageIndexPosition)
                .append(organisationNumber, that.organisationNumber)
                .append(businessType,that.businessType)
                .append(sicCodes,that.sicCodes)
                .append(executiveOfficers,that.executiveOfficers)
                .append(addressForm,that.addressForm)
                .append(dateOfIncorporation, that.dateOfIncorporation)
                .append(sicCodes, that.sicCodes)
                .append(executiveOfficers, that.executiveOfficers)
                .append(organisationAddress, that.organisationAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(triedToSave)
                .append(organisationTypeId)
                .append(organisationSearchName)
                .append(searchOrganisationId)
                .append(organisationSearching)
                .append(manualEntry)
                .append(organisationSearchResults)
                .append(organisationName)
                .append(searchPageIndexPosition)
                .append(organisationNumber)
                .append(businessType)
                .append(sicCodes)
                .append(executiveOfficers)
                .append(addressForm)
                .append(dateOfIncorporation)
                .append(sicCodes)
                .append(executiveOfficers)
                .append(organisationAddress)
                .toHashCode();
    }
}
