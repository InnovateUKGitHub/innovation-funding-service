package org.innovateuk.ifs.organisation.viewmodel;

import java.util.Optional;

public class ConfirmResearchOrganisationEligibilityViewModel {

    private long competitionId;
    private Long organisationId;
    private String organisationName;

    public ConfirmResearchOrganisationEligibilityViewModel(long competitionId, Long organisationId, String organisationName) {
        this.competitionId = competitionId;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public Optional<Long> getOrganisationId() {
        return Optional.ofNullable(organisationId);
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Optional<String> getOrganisationName() {
        return Optional.ofNullable(organisationName);
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
