package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ApplicationFinance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    public ApplicationFinance() {
    }

    public ApplicationFinance(Application application, Organisation organisation) {
        this.application = application;
        this.organisation = organisation;
    }

    public ApplicationFinance(long id, Application application, Organisation organisation) {
        this.id = id;
        this.application = application;
        this.organisation = organisation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Organisation getOrganisation() {
        return organisation;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }
}
