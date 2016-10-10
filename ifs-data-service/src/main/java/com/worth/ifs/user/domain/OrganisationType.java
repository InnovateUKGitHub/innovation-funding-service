package com.worth.ifs.user.domain;

import javax.persistence.*;

@Entity
public class OrganisationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType parentOrganisationType;

    public OrganisationType(String name, OrganisationType parentOrganisationType) {
        this.name = name;
        this.parentOrganisationType = parentOrganisationType;
    }

    public OrganisationType() {
    	// no-arg constructor
    }

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

    public OrganisationType getParentOrganisationType() {
        return parentOrganisationType;
    }

    public void setParentOrganisationType(OrganisationType parentOrganisationType) {
        this.parentOrganisationType = parentOrganisationType;
    }

}
