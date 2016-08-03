package com.worth.ifs.project.consortiumoverview.viewmodel;

import java.util.List;

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
}
