package com.worth.ifs.project.consortiumoverview.viewmodel;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectConsortiumStatusViewModel {
    private Long projectId;
    private LeadPartnerModel leadPartner;
    private List<RegularPartnerModel> otherPartners;

    public ProjectConsortiumStatusViewModel(final Long projectId, final LeadPartnerModel leadPartner, final List<RegularPartnerModel> otherPartners) {
        this.projectId = projectId;
        this.leadPartner = leadPartner;
        this.otherPartners = otherPartners;
    }

    public Long getProjectId() {
        return projectId;
    }

    public LeadPartnerModel getLeadPartner() {
        return leadPartner;
    }

    public List<RegularPartnerModel> getOtherPartners() {
        return otherPartners;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ProjectConsortiumStatusViewModel rhs = (ProjectConsortiumStatusViewModel) obj;
        return new EqualsBuilder()
            .append(this.projectId, rhs.projectId)
            .append(this.leadPartner, rhs.leadPartner)
            .append(this.otherPartners, rhs.otherPartners)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(projectId)
            .append(leadPartner)
            .append(otherPartners)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("\nprojectId", projectId)
            .append("\nleadPartner", leadPartner)
            .append("\notherPartners", otherPartners)
            .toString();
    }
}
