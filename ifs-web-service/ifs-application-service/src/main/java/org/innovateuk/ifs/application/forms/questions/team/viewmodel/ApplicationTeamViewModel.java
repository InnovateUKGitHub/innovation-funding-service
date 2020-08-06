package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ApplicationTeamViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final String applicationName;
    private final long questionId;
    private final List<ApplicationTeamOrganisationViewModel> organisations;
    private final long loggedInUserId;
    private final boolean leadApplicant;
    private final boolean collaborationLevelSingle;
    private final boolean open;
    private final boolean complete;
    private final boolean ktpCompetition;
    private final ApplicationKtaInviteResource ktaInvite;

    public ApplicationTeamViewModel(long applicationId,
                                    String applicationName,
                                    String competitionName,
                                    long questionId,
                                    List<ApplicationTeamOrganisationViewModel> organisations,
                                    long loggedInUserId,
                                    boolean leadApplicant,
                                    boolean collaborationLevelSingle,
                                    boolean open,
                                    boolean complete,
                                    boolean ktpCompetition,
                                    ApplicationKtaInviteResource ktaInvite
                                    ) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.organisations = organisations;
        this.loggedInUserId = loggedInUserId;
        this.leadApplicant = leadApplicant;
        this.collaborationLevelSingle = collaborationLevelSingle;
        this.open = open;
        this.complete = complete;
        this.ktpCompetition = ktpCompetition;
        this.ktaInvite = ktaInvite;
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

    public long getQuestionId() {
        return questionId;
    }

    public List<ApplicationTeamOrganisationViewModel> getOrganisations() {
        return organisations;
    }

    public long getLoggedInUserId() {
        return loggedInUserId;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isCollaborationLevelSingle() {
        return collaborationLevelSingle;
    }

    public ApplicationKtaInviteResource getKtaInvite() {
        return ktaInvite;
    }

    public ApplicationTeamViewModel openAddTeamMemberForm(long organisationId) {
        organisations.stream()
                .filter(partner -> partner.getId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }

    public boolean isReadOnly() {
        return !open || complete;
    }

    public boolean isAnyPendingInvites() {
        return organisations.stream()
                .flatMap(org -> org.getRows().stream())
                .anyMatch(ApplicationTeamRowViewModel::isInvite);
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public Long getKtaInvitePendingDays() {
        if (ktaInvite == null || InviteStatus.SENT != ktaInvite.getStatus()) {
            return null;
        }

        return Duration.between(ktaInvite.getSentOn().toInstant(), Instant.now()).toDays();
    }
}
