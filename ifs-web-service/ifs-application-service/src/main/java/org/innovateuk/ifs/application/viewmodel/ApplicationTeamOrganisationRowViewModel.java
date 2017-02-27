package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for an organisation in the Application Team view.
 */
public class ApplicationTeamOrganisationRowViewModel {

    private long id;
    private String name;
    private boolean lead;
    private List<ApplicationTeamApplicantRowViewModel> applicants;
    private boolean editable;

    public ApplicationTeamOrganisationRowViewModel(long id,
                                                   String name,
                                                   boolean lead,
                                                   List<ApplicationTeamApplicantRowViewModel> applicants,
                                                   boolean editable) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.applicants = applicants;
        this.editable = editable;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }

    public List<ApplicationTeamApplicantRowViewModel> getApplicants() {
        return applicants;
    }

    public boolean isEditable() {
        return editable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationTeamOrganisationRowViewModel that = (ApplicationTeamOrganisationRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(lead, that.lead)
                .append(editable, that.editable)
                .append(name, that.name)
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(lead)
                .append(applicants)
                .append(editable)
                .toHashCode();
    }
}