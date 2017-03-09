package org.innovateuk.ifs.finance.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.user.domain.OrganisationType;

import javax.persistence.*;

/**
 * Reference data that describes the maximum funding level that can be applied for.
 */
@Entity
@Immutable
public class GrantClaimMaximum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionTypeId", referencedColumnName="id")
    private CompetitionType competitionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryId", referencedColumnName="id")
    private ResearchCategory researchCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisationTypeId", referencedColumnName="id")
    private OrganisationType organisationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisationSizeId", referencedColumnName="id")
    private OrganisationSize organisationSize;

    private Integer maximum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public ResearchCategory getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategory researchCategory) {
        this.researchCategory = researchCategory;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
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
