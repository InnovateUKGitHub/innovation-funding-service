package org.innovateuk.ifs.organisation.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * Stores the SIC code from the companies house API which returns a list of strings for SIC codes.
 */
@Entity
@Table(name ="sic_code")
public class SicCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sicCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SicCode sicCode1 = (SicCode) o;
        return id.equals(sicCode1.id) &&
                sicCode.equals(sicCode1.sicCode) &&
                organisation.equals(sicCode1.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sicCode, organisation);
    }
}
