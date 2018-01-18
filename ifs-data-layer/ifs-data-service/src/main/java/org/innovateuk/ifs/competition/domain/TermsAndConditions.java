package org.innovateuk.ifs.competition.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a set of Terms and Conditions, comprising a name, a version, and an identifier for the template to use
 * for those terms and conditions.
 */
@Entity
@Immutable
public class TermsAndConditions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @SuppressWarnings("unused")
    private String name;

    @SuppressWarnings("unused")
    private String template;

    @SuppressWarnings("unused")
    private String version;

    public Long getId() {
        return id;
    }

    /**
     * Setter for MapStruct
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public String getVersion() {
        return version;
    }
}
