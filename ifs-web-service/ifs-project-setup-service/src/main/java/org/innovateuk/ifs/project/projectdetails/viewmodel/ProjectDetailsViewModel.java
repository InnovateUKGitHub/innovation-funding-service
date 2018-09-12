package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private UserResource currentUser;
    private List<Long> usersPartnerOrganisations;
    private List<OrganisationResource> organisations;
    private List<PartnerOrganisationResource> partnerOrganisations;

    private OrganisationResource leadOrganisation;
    private ApplicationResource app;
    private CompetitionResource competition;
    private boolean allProjectDetailsFinanceContactsAndProjectLocationsAssigned;

    private boolean monitoringOfficerAssigned;
    private boolean spendProfileGenerated;
    private boolean grantOfferLetterGenerated;
    private ProjectUserResource projectManager;
    private boolean readOnlyView;

    private Map<Long, ProjectUserResource> financeContactsByOrganisationId;
    private boolean userLeadPartner;

    public ProjectDetailsViewModel(ProjectResource project, UserResource currentUser,
                                   List<Long> usersPartnerOrganisations,
                                   List<OrganisationResource> organisations,
                                   List<PartnerOrganisationResource> partnerOrganisations,
                                   OrganisationResource leadOrganisation,
                                   ApplicationResource app,
                                   List<ProjectUserResource> projectUsers,
                                   CompetitionResource competition,
                                   boolean userIsLeadPartner,
                                   boolean allProjectDetailsFinanceContactsAndProjectLocationsAssigned,
                                   ProjectUserResource projectManager,
                                   boolean monitoringOfficerAssigned,
                                   boolean spendProfileGenerated,
                                   boolean grantOfferLetterGenerated,
                                   boolean readOnlyView) {
        this.project = project;
        this.currentUser = currentUser;
        this.usersPartnerOrganisations = usersPartnerOrganisations;
        this.partnerOrganisations = partnerOrganisations;
        this.organisations = organisations;
        this.leadOrganisation = leadOrganisation;
        this.app = app;
        this.competition = competition;
        this.allProjectDetailsFinanceContactsAndProjectLocationsAssigned = allProjectDetailsFinanceContactsAndProjectLocationsAssigned;
        this.monitoringOfficerAssigned = monitoringOfficerAssigned;
        this.spendProfileGenerated = spendProfileGenerated;
        this.grantOfferLetterGenerated = grantOfferLetterGenerated;
        this.projectManager = projectManager;
        this.readOnlyView = readOnlyView;
        List<ProjectUserResource> financeRoles = simpleFilter(projectUsers, ProjectUserResource::isFinanceContact);
        this.financeContactsByOrganisationId = simpleToMap(financeRoles, ProjectUserResource::getOrganisation, Function.identity());
        this.userLeadPartner = userIsLeadPartner;
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

    public ApplicationResource getApp() {
        return app;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ProjectUserResource getFinanceContactForPartnerOrganisation(Long organisationId) {
        return financeContactsByOrganisationId.get(organisationId);
    }

    public Map<Long, ProjectUserResource> getFinanceContactsByOrganisationId() {
        return financeContactsByOrganisationId;
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

    public boolean isAllProjectDetailsFinanceContactsAndProjectLocationsAssigned() {
        return allProjectDetailsFinanceContactsAndProjectLocationsAssigned;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public boolean isSpendProfileGenerated() {
        return spendProfileGenerated;
    }

    public boolean isGrantOfferLetterGenerated() {
        return grantOfferLetterGenerated;
    }

    public ProjectUserResource getProjectManager() {
        return projectManager;
    }
}
