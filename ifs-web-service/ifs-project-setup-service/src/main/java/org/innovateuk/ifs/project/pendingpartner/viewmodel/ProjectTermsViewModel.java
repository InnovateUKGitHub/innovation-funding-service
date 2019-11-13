package org.innovateuk.ifs.project.pendingpartner.viewmodel;

public class ProjectTermsViewModel {
    private final long projectId;
    private final long organisationId;
    private final String competitionTermsTemplate;
    private final boolean termsAccepted;
    private final boolean showHeaderAndFooter;

    public ProjectTermsViewModel(long projectId,
                                 long organisationId,
                                 String competitionTermsTemplate,
                                 boolean termsAccepted) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.showHeaderAndFooter = true;
        this.termsAccepted = termsAccepted;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public String getCompetitionTermsTemplate() {
        return competitionTermsTemplate;
    }

    public boolean isShowHeaderAndFooter() {
        return showHeaderAndFooter;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }
}
