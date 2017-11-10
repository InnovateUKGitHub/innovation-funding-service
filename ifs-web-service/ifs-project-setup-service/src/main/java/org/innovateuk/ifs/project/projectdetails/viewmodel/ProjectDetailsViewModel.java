package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
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
    private List<OrganisationResource> partnerOrganisations;

    private OrganisationResource leadOrganisation;
    private ApplicationResource app;
    private CompetitionResource competition;
    private boolean projectDetailsCompleteAndAllFinanceContactsAssigned;
    private boolean spendProfileGenerated;
    private boolean grantOfferLetterGenerated;
    private ProjectUserResource projectManager;
    private boolean readOnlyView;

    private Map<Long, ProjectUserResource> financeContactsByOrganisationId;
    private boolean userLeadPartner;

    public ProjectDetailsViewModel(ProjectResource project, UserResource currentUser,
                                   List<Long> usersPartnerOrganisations,
                                   List<OrganisationResource> partnerOrganisations,
                                   OrganisationResource leadOrganisation,
                                   ApplicationResource app,
                                   List<ProjectUserResource> projectUsers,
                                   CompetitionResource competition,
                                   boolean userIsLeadPartner,
                                   boolean projectDetailsCompleteAndAllFinanceContactsAssigned,
                                   ProjectUserResource projectManager,
                                   boolean spendProfileGenerated,
                                   boolean grantOfferLetterGenerated,
                                   boolean readOnlyView) {
        this.project = project;
        this.currentUser = currentUser;
        this.usersPartnerOrganisations = usersPartnerOrganisations;
        this.partnerOrganisations = partnerOrganisations;
        this.leadOrganisation = leadOrganisation;
        this.app = app;
        this.competition = competition;
        this.projectDetailsCompleteAndAllFinanceContactsAssigned = projectDetailsCompleteAndAllFinanceContactsAssigned;
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

    public List<OrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
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

    public boolean isProjectDetailsCompleteAndAllFinanceContactsAssigned() {
        return projectDetailsCompleteAndAllFinanceContactsAssigned;
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
