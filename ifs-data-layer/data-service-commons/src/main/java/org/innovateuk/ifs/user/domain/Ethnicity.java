package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.commons.ZeroDowntime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * The ethnicity of a {@link User}.
 */
@ZeroDowntime(description = "delete", reference = "IFS-4105")
@Entity
public class Ethnicity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int priority;
    private boolean active;

    public Ethnicity() {
    }

    public Ethnicity(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isActive() {
        return active;
    }
}