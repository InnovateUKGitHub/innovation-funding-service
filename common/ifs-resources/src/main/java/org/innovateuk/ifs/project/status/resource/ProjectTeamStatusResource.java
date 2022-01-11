package org.innovateuk.ifs.project.status.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * A wrapping object that encompasses the status for the whole team.
 * Contains helper methods to only get lead or non-lead partners.  This is useful as lead is usually shown
 * differently from others in most view templates (e.g. first in list with more details).
 */
public class ProjectTeamStatusResource {
    private List<ProjectPartnerStatusResource> partnerStatuses;
    private ProjectState projectState;
    private boolean projectManagerAssigned;

    public List<ProjectPartnerStatusResource> getPartnerStatuses() {
        return partnerStatuses;
    }

    public void setPartnerStatuses(List<ProjectPartnerStatusResource> partnerStatuses) {
        this.partnerStatuses = partnerStatuses;
    }

    public void setProjectManagerAssigned(boolean projectManagerAssigned) {
        this.projectManagerAssigned = projectManagerAssigned;
    }

    public boolean isProjectManagerAssigned() {
        return projectManagerAssigned;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public void setProjectState(ProjectState projectState) {
        this.projectState = projectState;
    }

    @JsonIgnore
    public ProjectPartnerStatusResource getLeadPartnerStatus() {
        return partnerStatuses.stream().filter(ProjectPartnerStatusResource::isLead).findFirst().orElse(null);
    }

    @JsonIgnore
    public List<ProjectPartnerStatusResource> getOtherPartnersStatuses(){
        return partnerStatuses.stream().filter(status -> !status.isLead()).collect(Collectors.toList());
    }

    @JsonIgnore
    public boolean isLeadOrganisation(Long organisationId) {
        return !partnerStatuses.isEmpty() && partnerStatuses.get(0).getOrganisationId().equals(organisationId);
    }

    @JsonIgnore
    public Optional<ProjectPartnerStatusResource> getPartnerStatusForOrganisation(Long organisationId) {
        return simpleFindFirst(partnerStatuses, status -> organisationId.equals(status.getOrganisationId()));
    }

    @JsonIgnore
    public boolean checkForAllPartners(Predicate<ProjectPartnerStatusResource> partnerStatusPredicate) {
        return getPartnerStatuses().stream().allMatch(partnerStatusPredicate);
    }

    @JsonIgnore
    public boolean checkForOtherPartners(Predicate<ProjectPartnerStatusResource> partnerStatusPredicate) {
        return getOtherPartnersStatuses().stream().allMatch(partnerStatusPredicate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectTeamStatusResource that = (ProjectTeamStatusResource) o;

        return new EqualsBuilder()
                .append(partnerStatuses, that.partnerStatuses)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(partnerStatuses)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("partnerStatuses", partnerStatuses)
                .toString();
    }
}
