package org.innovateuk.ifs.finance.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.OrganisationType;

import javax.persistence.*;

/**
 * Reference data that describes the maximum funding level that can be applied for.
 */
@Entity(name = "grant_claim_maximum_new")
@Immutable
public class GrantClaimMaximum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    private ResearchCategory researchCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationTypeId", referencedColumnName = "id")
    private OrganisationType organisationType;

    private Integer def;

    private Integer small;

    private Integer medium;

    private Integer large;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(final Competition competition) {
        this.competition = competition;
    }

    public ResearchCategory getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(final ResearchCategory researchCategory) {
        this.researchCategory = researchCategory;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(final OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public Integer getDef() {
        return def;
    }

    public void setDef(final Integer def) {
        this.def = def;
    }

    public Integer getSmall() {
        return small;
    }

    public void setSmall(final Integer small) {
        this.small = small;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(final Integer medium) {
        this.medium = medium;
    }

    public Integer getLarge() {
        return large;
    }

    public void setLarge(final Integer large) {
        this.large = large;
    }
}
