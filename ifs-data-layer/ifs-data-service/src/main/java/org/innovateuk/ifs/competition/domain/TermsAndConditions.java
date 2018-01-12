package org.innovateuk.ifs.competition.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Immutable
public class TermsAndConditions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String template;

    private String version;

    public Long getId() {
        return id;
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
