package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
public class CompetitionType {

    private static final String SECTOR = "Sector";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Boolean stateAid;
    private Boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="templateCompetitionId", referencedColumnName="id")
    private Competition template;

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

    public Boolean getStateAid() {
        return stateAid;
    }

    public void setStateAid(Boolean stateAid) {
        this.stateAid = stateAid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Competition getTemplate() { return template; }

    public void setTemplate(Competition template) { this.template = template; }

    public boolean isSector() {
        return this.name.equals(SECTOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionType that = (CompetitionType) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(stateAid, that.stateAid)
                .append(active, that.active)
                .append(template, that.template)
                .append(competitions, that.competitions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(stateAid)
                .append(active)
                .append(template)
                .append(competitions)
                .toHashCode();
    }
}
