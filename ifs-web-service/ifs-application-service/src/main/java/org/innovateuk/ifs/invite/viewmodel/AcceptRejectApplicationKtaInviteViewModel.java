package org.innovateuk.ifs.invite.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

public class AcceptRejectApplicationKtaInviteViewModel implements BaseAnalyticsViewModel {

    private long applicationId;
    private String applicationName;
    private String competitionName;
    private String leadOrganisationName;
    private String leadApplicantName;
    private String hash;

    public AcceptRejectApplicationKtaInviteViewModel(long applicationId,
                                                     String applicationName,
                                                     String competitionName,
                                                     String leadOrganisationName,
                                                     String leadApplicantName,
                                                     String hash) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.leadOrganisationName = leadOrganisationName;
        this.leadApplicantName = leadApplicantName;
        this.hash = hash;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public String getHash() {
        return hash;
    }
}
