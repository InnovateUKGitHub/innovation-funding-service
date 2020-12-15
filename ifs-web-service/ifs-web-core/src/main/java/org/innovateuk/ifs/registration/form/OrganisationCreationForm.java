package org.innovateuk.ifs.registration.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Object to store the data that is used for the companies house form, while creating a new application.
 */

@FieldRequiredIf(required = "organisationSearchName", argument = "organisationSearching", predicate = true, message = "{validation.standard.organisationsearchname.required}")
@FieldRequiredIf(required = "organisationName", argument = "manualEntry", predicate = true, message = "{validation.standard.organisationname.required}")
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

    private int searchPageIndexPosition = 1;

    private int totalSearchResults = 0;

    public OrganisationCreationForm() {
        this.organisationSearchResults = new ArrayList<>();
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
                .toHashCode();
    }
}
