package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ApplicationTeamManagementOrganisationRowViewModel {

    private long id;
    private String name;
    private boolean lead;
    private List<ApplicationTeamManagementApplicantRowViewModel> applicants;

    public ApplicationTeamManagementOrganisationRowViewModel(long id, String name, boolean lead, List<ApplicationTeamManagementApplicantRowViewModel> applicants) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.applicants = applicants;
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

    public List<ApplicationTeamManagementApplicantRowViewModel> getApplicants() {
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

        ApplicationTeamManagementOrganisationRowViewModel that = (ApplicationTeamManagementOrganisationRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(lead, that.lead)
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
                .toHashCode();
    }
}
