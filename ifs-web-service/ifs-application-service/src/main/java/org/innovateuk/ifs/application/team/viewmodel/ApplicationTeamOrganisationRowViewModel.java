package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for an organisation in the Application Team view.
 */
public class ApplicationTeamOrganisationRowViewModel {

    private Long organisationId;
    private Long inviteOrganisationId;
    private String name;
    private String type;
    private boolean lead;
    private List<ApplicationTeamApplicantRowViewModel> applicants;
    private boolean editable;

    public ApplicationTeamOrganisationRowViewModel(Long organisationId, Long inviteOrganisationId, String name, String type, boolean lead, List<ApplicationTeamApplicantRowViewModel> applicants, boolean editable) {
        this.organisationId = organisationId;
        this.inviteOrganisationId = inviteOrganisationId;
        this.name = name;
        this.type = type;
        this.lead = lead;
        this.applicants = applicants;
        this.editable = editable;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getInviteOrganisationId() {
        return inviteOrganisationId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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
                .append(lead, that.lead)
                .append(editable, that.editable)
                .append(organisationId, that.organisationId)
                .append(inviteOrganisationId, that.inviteOrganisationId)
                .append(name, that.name)
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationId)
                .append(inviteOrganisationId)
                .append(name)
                .append(lead)
                .append(applicants)
                .append(editable)
                .toHashCode();
    }
}