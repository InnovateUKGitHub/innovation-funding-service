package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.*;

@Entity
@DiscriminatorValue("org.innovateuk.ifs.competition.domain.Competition")
public class CompetitionCategoryLink<C extends Category> extends CategoryLink<Competition, C> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Competition competition;


    // TODO public to support mapstruct, but should be package protected
    public CompetitionCategoryLink() {
        // default constructor
    }

    public CompetitionCategoryLink(Competition competition, C category) {
        super(category);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }

        this.competition = competition;
    }

    public Competition getEntity() {
        return competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionCategoryLink that = (CompetitionCategoryLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(competition, that.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(competition)
                .toHashCode();
    }
}