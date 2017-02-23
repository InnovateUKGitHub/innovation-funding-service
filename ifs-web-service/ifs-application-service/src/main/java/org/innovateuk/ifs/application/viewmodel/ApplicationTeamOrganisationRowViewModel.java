package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for an organisation in the Application Team view.
 */
public class ApplicationTeamOrganisationRowViewModel {

    private String name;
    private boolean lead;
    private List<ApplicationTeamApplicantRowViewModel> applicants;

    public ApplicationTeamOrganisationRowViewModel(String name, boolean lead, List<ApplicationTeamApplicantRowViewModel> applicants) {
        this.name = name;
        this.lead = lead;
        this.applicants = applicants;
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
                .append(name, that.name)
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(lead)
                .append(applicants)
                .toHashCode();
    }
}