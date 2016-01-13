package com.worth.ifs.invite.domain;

import com.worth.ifs.application.domain.Application;

import javax.persistence.*;

@Entity
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;

    @ManyToOne
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "inviteOrganisationId", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    private String hash;

    private Integer status;
}
