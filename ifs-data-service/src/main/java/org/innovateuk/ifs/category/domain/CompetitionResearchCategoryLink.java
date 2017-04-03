package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("org.innovateuk.ifs.competition.domain.Competition#researchCategory")
public class CompetitionResearchCategoryLink extends CategoryLink<Competition, ResearchCategory> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Competition competition;

    CompetitionResearchCategoryLink() {
    }

    public CompetitionResearchCategoryLink(Competition competition, ResearchCategory category) {
        super(category);

        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }

        this.competition = competition;
    }

    @Override
    public Competition getEntity() {
        return competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionResearchCategoryLink that = (CompetitionResearchCategoryLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(competition.getId(), that.competition.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(competition.getId())
                .toHashCode();
    }
}
