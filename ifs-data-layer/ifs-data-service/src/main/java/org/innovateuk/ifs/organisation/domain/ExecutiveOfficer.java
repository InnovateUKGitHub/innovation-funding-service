package org.innovateuk.ifs.organisation.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * Stores the Executive officer name string from the companies house list officers api call.
 */
@Entity
public class ExecutiveOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutiveOfficer that = (ExecutiveOfficer) o;
        return id.equals(that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(organisation, that.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, organisation);
    }
}
