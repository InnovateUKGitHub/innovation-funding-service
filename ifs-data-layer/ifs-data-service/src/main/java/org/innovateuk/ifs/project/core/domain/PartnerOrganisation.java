package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.user.domain.ProcessActivity;

import javax.persistence.*;
import java.util.Optional;

/**
 * Represents an Organisation taking part in a Project in the capacity of a Partner Organisation
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "project_id", "organisation_id" } ) } )
public class PartnerOrganisation implements ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "partnerOrganisation")
    private PendingPartnerProgress pendingPartnerProgress;

    private boolean leadOrganisation;

    private String postcode;

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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public PendingPartnerProgress getPendingPartnerProgress() {
        return pendingPartnerProgress;
    }

    public boolean isPendingPartner() {
        return Optional.ofNullable(getPendingPartnerProgress())
                .map(p -> !p.isComplete())
                .orElse(false);
    }
}
