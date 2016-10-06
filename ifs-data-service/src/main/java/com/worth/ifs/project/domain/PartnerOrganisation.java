package com.worth.ifs.project.domain;

import com.worth.ifs.invite.domain.ProcessActivity;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;

/**
 * Represents an Organisation taking part in a Project in the capacity of a Partner Organisation
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "project_id", "organisation_id" } ) } )
public class PartnerOrganisation implements ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    private boolean leadOrganisation;

    public PartnerOrganisation() {
        // for ORM use
    }

    public PartnerOrganisation(Project project, Organisation organisation, boolean leadOrganisation) {
        this.project = project;
        this.organisation = organisation;
        this.leadOrganisation = leadOrganisation;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }
}
