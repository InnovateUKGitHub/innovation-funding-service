package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.Organisation;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    private boolean leadOrganisation;

    @ZeroDowntime(reference = "IFS-3470", description = "This will be removed in subsequent release")
    @Column(name = "post_code")
    private String postCode;

    @ZeroDowntime(reference = "IFS-3470", description = "Remove the Column annotation. Its just added to satisfy Hibernate at the moment")
    @Column(name = "postcode")
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

/*    @Deprecated
    @ZeroDowntime(reference = "IFS-3470", description = "This still needs to be there else Hibernate complains - but should not be used anywhere. Hence marked with @Deprecated")
    public String getPostCode() {
        return postCode;
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-3470", description = "This still needs to be there else Hibernate complains - but should not be used anywhere. Hence marked with @Deprecated")
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }*/

    @ZeroDowntime(reference = "IFS-3470", description = "Currently this will read the old column.")
    public String getPostcode() {
        return postCode; // returns the old column postCode (not postcode)
    }

    @ZeroDowntime(reference = "IFS-3470", description = "Currently this will write to both old and new.")
    public void setPostcode(String postcode) {
        this.postCode = postcode; // Writes to old
        this.postcode = postcode; // Writes to new
    }
}
