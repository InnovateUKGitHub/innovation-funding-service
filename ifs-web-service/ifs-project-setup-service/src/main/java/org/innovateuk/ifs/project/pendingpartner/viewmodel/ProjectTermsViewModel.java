package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ProjectTermsViewModel {
    private final long projectId;
    private final long organisationId;
    private final String competitionTermsTemplate;
    private final boolean termsAccepted;
    private final ZonedDateTime termsAcceptedOn;

    public ProjectTermsViewModel(long projectId,
                                 long organisationId,
                                 String competitionTermsTemplate,
                                 boolean termsAccepted,
                                 ZonedDateTime termsAcceptedOn) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedOn = termsAcceptedOn;
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

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public Optional<ZonedDateTime> getTermsAcceptedOn() {
        return Optional.ofNullable(termsAcceptedOn);
    }
}
