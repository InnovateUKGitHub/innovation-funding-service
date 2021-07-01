package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;

import javax.persistence.*;
import java.util.List;

@Entity
public class CompetitionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean active;

    @OneToMany(mappedBy="competitionType", fetch = FetchType.LAZY)
    private List<Competition> competitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CompetitionTypeEnum getCompetitionTypeEnum() {
        return CompetitionTypeEnum.fromText(getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionType that = (CompetitionType) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(active, that.active)
                .append(competitions, that.competitions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(active)
                .append(competitions)
                .toHashCode();
    }
}
