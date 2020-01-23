package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class ProjectDetailsViewModel {
    private ProjectResource project;
    private UserResource currentUser;
    private List<Long> usersPartnerOrganisations;
    private List<OrganisationResource> organisations;
    private List<PartnerOrganisationResource> partnerOrganisations;

    private OrganisationResource leadOrganisation;

    private boolean spendProfileGenerated;
    private boolean grantOfferLetterGenerated;
    private boolean readOnlyView;

    private boolean userLeadPartner;
    private boolean collaborativeProject;

    public ProjectDetailsViewModel(ProjectResource project, UserResource currentUser,
                                         List<Long> usersPartnerOrganisations,
                                         List<OrganisationResource> organisations,
                                         List<PartnerOrganisationResource> partnerOrganisations,
                                         OrganisationResource leadOrganisation,
                                         boolean userIsLeadPartner,
                                         boolean spendProfileGenerated,
                                         boolean grantOfferLetterGenerated,
                                         boolean readOnlyView) {
        this.project = project;
        this.currentUser = currentUser;
        this.usersPartnerOrganisations = usersPartnerOrganisations;
        this.partnerOrganisations = partnerOrganisations;
        this.organisations = organisations;
        this.leadOrganisation = leadOrganisation;
        this.spendProfileGenerated = spendProfileGenerated;
        this.grantOfferLetterGenerated = grantOfferLetterGenerated;
        this.readOnlyView = readOnlyView;
        this.userLeadPartner = userIsLeadPartner;
        this.collaborativeProject = project.isCollaborativeProject();
    }

    public ProjectResource getProject() {
        return project;
    }

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public List<OrganisationResource> getOrganisations() {
        return organisations;
    }

    public String getPostcodeForPartnerOrganisation(Long organisationId) {
        return partnerOrganisations.stream()
                .filter(partnerOrganisation ->  partnerOrganisation.getOrganisation().equals(organisationId))
                .findFirst()
                .map(PartnerOrganisationResource::getPostcode)
                .orElse(null);
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public boolean isUserLeadPartner() {
        return userLeadPartner;
    }

    public boolean isUserPartnerInOrganisation(Long organisationId) {
        return usersPartnerOrganisations.contains(organisationId);
    }

    public boolean isReadOnly() { return readOnlyView; }

    public boolean isSpendProfileGenerated() {
        return spendProfileGenerated;
    }

    public boolean isGrantOfferLetterGenerated() {
        return grantOfferLetterGenerated;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    /*
     * View model logic.
     * */

    public boolean isProjectLive() {
        return project.getProjectState().isLive();
    }
}