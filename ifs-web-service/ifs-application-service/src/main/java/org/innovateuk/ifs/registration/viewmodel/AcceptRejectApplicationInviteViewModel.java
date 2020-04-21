package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

public class AcceptRejectApplicationInviteViewModel implements BaseAnalyticsViewModel {

    private long applicationId;
    private long competitionId;
    private String competitionName;
    private String leadOrganisationName;
    private String leadApplicantName;
    private String inviteOrganisationName;
    private String leadApplicantEmail;
    private boolean inviteOrganisationExists;
    private boolean leadOrganisation;

    public AcceptRejectApplicationInviteViewModel(final long applicationId,
                                                  final long competitionId, final String competitionName,
                                                  final String leadOrganisationName, final String leadApplicantName,
                                                  final String inviteOrganisationName,
                                                  final String leadApplicantEmail,
                                                  final boolean inviteOrganisationExists,
                                                  final boolean leadOrganisation) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.leadOrganisationName = leadOrganisationName;
        this.leadApplicantName = leadApplicantName;
        this.inviteOrganisationName = inviteOrganisationName;
        this.leadApplicantEmail = leadApplicantEmail;
        this.inviteOrganisationExists = inviteOrganisationExists;
        this.leadOrganisation = leadOrganisation;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isInviteOrganisationExists() {
        return inviteOrganisationExists;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }
}


