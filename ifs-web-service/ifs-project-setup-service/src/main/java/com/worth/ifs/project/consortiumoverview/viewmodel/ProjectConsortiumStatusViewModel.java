package com.worth.ifs.project.consortiumoverview.viewmodel;

import java.util.List;

public class ProjectConsortiumStatusViewModel {

    private LeadPartnerModel leadPartner;
    private List<RegularPartnerModel> otherPartners;

    public ProjectConsortiumStatusViewModel(final LeadPartnerModel leadPartner, final List<RegularPartnerModel> otherPartners) {
        this.leadPartner = leadPartner;
        this.otherPartners = otherPartners;
    }

    public LeadPartnerModel getLeadPartner() {
        return leadPartner;
    }

    public List<RegularPartnerModel> getOtherPartners() {
        return otherPartners;
    }
}
