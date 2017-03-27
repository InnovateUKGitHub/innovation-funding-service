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
    private boolean projectDetailsSubmitted;
    private ProjectUserResource projectManager;
    private boolean submissionAllowed;
    private boolean isFinanceContactSubmitted;
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
                                   boolean projectDetailsSubmitted,
                                   ProjectUserResource projectManager,
                                   boolean submissionAllowed,
                                   boolean readOnlyView) {
        this.project = project;
        this.currentUser = currentUser;
        this.usersPartnerOrganisations = usersPartnerOrganisations;
        this.partnerOrganisations = partnerOrganisations;
        this.leadOrganisation = leadOrganisation;
        this.app = app;
        this.competition = competition;
        this.projectDetailsSubmitted = projectDetailsSubmitted;
        this.projectManager = projectManager;
        this.submissionAllowed = submissionAllowed;
        this.readOnlyView = readOnlyView;

        List<ProjectUserResource> financeRoles = simpleFilter(projectUsers, ProjectUserResource::isFinanceContact);
        this.financeContactsByOrganisationId = simpleToMap(financeRoles, ProjectUserResource::getOrganisation, Function.identity());
        this.isFinanceContactSubmitted = usersPartnerOrganisations.stream().anyMatch(organisation -> financeContactsByOrganisationId.containsKey(organisation));
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

    public boolean isProjectDetailsSubmitted() {
        return projectDetailsSubmitted;
    }

    public ProjectUserResource getProjectManager() {
        return projectManager;
    }

    public boolean isSubmissionAllowed() {
        return submissionAllowed;
    }

    public boolean isFinanceContactSubmitted() {
        return isFinanceContactSubmitted;
    }

    public boolean isSubmitProjectDetailsAllowed() {
        return userLeadPartner && submissionAllowed && !projectDetailsSubmitted;
    }

    public boolean isAnySectionIncomplete() {
        return userLeadPartner && !submissionAllowed && !projectDetailsSubmitted;
    }
}
