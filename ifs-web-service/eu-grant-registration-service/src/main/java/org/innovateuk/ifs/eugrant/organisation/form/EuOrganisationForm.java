package org.innovateuk.ifs.eugrant.organisation.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;

@FieldRequiredIf(required = "organisationSearchName", argument = "organisationSearching", predicate = true, message = "{validation.standard.organisationsearchname.required}")
@FieldRequiredIf(required = "organisationName", argument = "manualEntry", predicate = true, message = "{validation.standard.organisationname.required}")
public class EuOrganisationForm {

    private String organisationSearchName;
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
