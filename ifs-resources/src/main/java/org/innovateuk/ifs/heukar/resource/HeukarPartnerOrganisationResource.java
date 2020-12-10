package org.innovateuk.ifs.heukar.resource;

public class HeukarPartnerOrganisationResource {
    private Long id;
    private Long applicationId;

    private HeukarPartnerOrganisationTypeEnum heukarPartnerOrganisationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public HeukarPartnerOrganisationTypeEnum getHeukarPartnerOrganisationType() {
        return heukarPartnerOrganisationType;
    }

    public void setHeukarPartnerOrganisationType(HeukarPartnerOrganisationTypeEnum heukarPartnerOrganisationType) {
        this.heukarPartnerOrganisationType = heukarPartnerOrganisationType;
    }

}
