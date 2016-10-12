package com.worth.ifs.invite.domain;

import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
* The InviteOrganisation entity serves the purpose of grouping Invites by organisation name entered in the application.
* When an actual organisation exists the InviteOrganisation can link the associated Invites to that organisation.
* */

@Entity
public class InviteOrganisation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String organisationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    @OneToMany(mappedBy = "inviteOrganisation")
    private List<ApplicationInvite> invites;

    public InviteOrganisation() {
    	// no-arg constructor
    }

    public InviteOrganisation(String organisationName, Organisation organisation, List<ApplicationInvite> invites) {
        this.organisationName = organisationName;
        this.organisation = organisation;
        this.invites = invites;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public List<ApplicationInvite> getInvites() {
        return (invites == null)? new ArrayList<>() : invites;
    }

    public void setInvites(List<ApplicationInvite> invites) {
        this.invites = invites;
    }
}
