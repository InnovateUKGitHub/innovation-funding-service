package com.worth.ifs.invite.domain;

import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;
import java.util.List;

@Entity
public class InviteOrganisation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String organisationName;

    @ManyToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    @OneToMany(mappedBy = "inviteOrganisation")
    private List<Invite> invites;
}
