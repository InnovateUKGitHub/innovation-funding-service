package org.innovateuk.ifs.organisation.viewmodel;

import java.util.Optional;

public class ConfirmResearchOrganisationEligibilityViewModel {

    private long competitionId;
    private String organisationName;

    public ConfirmResearchOrganisationEligibilityViewModel(long competitionId, String organisationName) {
        this.competitionId = competitionId;
        this.organisationName = organisationName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public Optional<String> getOrganisationName() {
        return Optional.ofNullable(organisationName);
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
