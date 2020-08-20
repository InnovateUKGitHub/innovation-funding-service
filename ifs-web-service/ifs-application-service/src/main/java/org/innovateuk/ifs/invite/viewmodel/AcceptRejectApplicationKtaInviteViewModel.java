package org.innovateuk.ifs.invite.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

public class AcceptRejectApplicationKtaInviteViewModel implements BaseAnalyticsViewModel {

    private long applicationId;
    private String applicationName;
    private String competitionName;
    private String leadOrganisationName;
    private String leadApplicantName;

    public AcceptRejectApplicationKtaInviteViewModel(long applicationId,
                                                     String applicationName,
                                                     String competitionName,
                                                     String leadOrganisationName,
                                                     String leadApplicantName) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.leadOrganisationName = leadOrganisationName;
        this.leadApplicantName = leadApplicantName;
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

}
