package org.innovateuk.ifs.finance.resource;

public class GrantClaimMaximumResource {

    private Long id;

    private Long researchCategory;

    private Long organisationType;

    private OrganisationSize organisationSize;

    private Integer maximum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(Long researchCategory) {
        this.researchCategory = researchCategory;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }
}
