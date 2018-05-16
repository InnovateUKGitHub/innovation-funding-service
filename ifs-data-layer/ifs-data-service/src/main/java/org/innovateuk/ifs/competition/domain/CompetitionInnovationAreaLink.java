package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.domain.CategoryLink;
import org.innovateuk.ifs.category.domain.InnovationArea;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("org.innovateuk.ifs.competition.domain.Competition#innovationArea")
public class CompetitionInnovationAreaLink extends CategoryLink<Competition, InnovationArea> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Competition competition;

    protected CompetitionInnovationAreaLink() {

    }

    public CompetitionInnovationAreaLink(Competition competition, InnovationArea category) {
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

        CompetitionInnovationAreaLink that = (CompetitionInnovationAreaLink) o;

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
