package org.innovateuk.ifs.finance.domain;

import org.hibernate.annotations.ColumnDefault;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.persistence.*;
import java.util.List;

/**
 * Reference data that describes the maximum funding levels that can be applied for.
 */
@Entity
public class GrantClaimMaximum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "grantClaimMaximums",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Competition> competitions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    private ResearchCategory researchCategory;

    @Column(name = "organisation_size_id")
    private OrganisationSize organisationSize;

    @Enumerated(EnumType.STRING)
    private FundingRules fundingRules;

    @Column(columnDefinition = "tinyint(4)")
    private Integer maximum;

    public GrantClaimMaximum() {}

    public GrantClaimMaximum(ResearchCategory researchCategory, OrganisationSize organisationSize, FundingRules fundingRules, Integer maximum) {
        this.researchCategory = researchCategory;
        this.organisationSize = organisationSize;
        this.maximum = maximum;
        this.fundingRules = fundingRules;
    }

    public GrantClaimMaximum(ResearchCategory researchCategory, OrganisationSize organisationSize, Integer maximum) {
        this(researchCategory, organisationSize, null, maximum);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public ResearchCategory getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategory researchCategory) {
        this.researchCategory = researchCategory;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }

    public GrantClaimMaximum withFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
        return this;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

}
