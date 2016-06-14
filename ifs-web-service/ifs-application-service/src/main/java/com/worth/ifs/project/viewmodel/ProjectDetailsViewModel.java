package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private UserResource currentUser;
    private Long currentOrganisation;
    private List<OrganisationResource> partnerOrganisations;
    private ApplicationResource app;
    private CompetitionResource competition;
    private Map<Long, ProjectUserResource> financeContactsByOrganisationId;
    private boolean userLeadPartner;

    public ProjectDetailsViewModel(ProjectResource project, UserResource currentUser, List<Long> currentOrganisations, List<OrganisationResource> partnerOrganisations, ApplicationResource app, List<ProjectUserResource> projectUsers, CompetitionResource competition, boolean userIsLeadPartner) {
        this.project = project;
        this.currentUser = currentUser;
        this.currentOrganisation = currentOrganisation;
        this.partnerOrganisations = partnerOrganisations;
        this.app = app;
        this.competition = competition;
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

    public Long getCurrentOrganisation() {
        return currentOrganisation;
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

    public boolean isUserLeadPartner() {
        return userLeadPartner;
    }
}
