package org.innovateuk.ifs.eugrant.organisation.form;

import org.hibernate.validator.constraints.NotBlank;

public class OrganisationForm {
    @NotBlank(message = "{validation.standard.organisationsearchname.required}")
    // on empty value don't check pattern since then there already is a validation message.
    private String organisationSearchName;

    @NotBlank(message = "{validation.standard.organisationname.required}")
    private String organisationName;

    private String selectedOrganisationId;
    private boolean manualEntry = false;
    private boolean organisationSearching = false;

    public String getOrganisationSearchName() {
        return organisationSearchName;
    }

    public void setOrganisationSearchName(String organisationSearchName) {
        this.organisationSearchName = organisationSearchName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getSelectedOrganisationId() {
        return selectedOrganisationId;
    }

    public void setSelectedOrganisationId(String selectedOrganisationId) {
        this.selectedOrganisationId = selectedOrganisationId;
    }

    public boolean isManualEntry() {
        return manualEntry;
    }

    public void setManualEntry(boolean manualEntry) {
        this.manualEntry = manualEntry;
    }

    public boolean isOrganisationSearching() {
        return organisationSearching;
    }

    public void setOrganisationSearching(boolean organisationSearching) {
        this.organisationSearching = organisationSearching;
    }
}
