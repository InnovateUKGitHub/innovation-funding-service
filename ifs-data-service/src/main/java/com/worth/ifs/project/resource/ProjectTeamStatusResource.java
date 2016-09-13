package com.worth.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectTeamStatusResource {
    private List<ProjectPartnerStatusResource> partnerStatuses;

    public List<ProjectPartnerStatusResource> getPartnerStatuses() {
        return partnerStatuses;
    }

    public void setPartnerStatuses(List<ProjectPartnerStatusResource> partnerStatuses) {
        this.partnerStatuses = partnerStatuses;
    }

    @JsonIgnore
    public ProjectPartnerStatusResource getLeadPartnerStatus(){
        return partnerStatuses.stream().filter(status -> status instanceof ProjectLeadStatusResource).findFirst().orElse(null);
    }

    @JsonIgnore
    public List<ProjectPartnerStatusResource> getOtherPartnersStatuses(){
        return partnerStatuses.stream().filter(status -> !(status instanceof ProjectLeadStatusResource)).collect(Collectors.toList());
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
