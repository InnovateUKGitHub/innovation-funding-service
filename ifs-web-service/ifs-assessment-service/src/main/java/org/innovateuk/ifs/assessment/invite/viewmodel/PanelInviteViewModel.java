package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;

import java.time.ZonedDateTime;

/**
 * ViewModel of a AssessmentPanelInvite.
 */
public class PanelInviteViewModel {

    private String panelInviteHash;
    private Long competitionId;
    private String competitionName;
    private ZonedDateTime panelDate;
    private boolean userLoggedIn;

    public PanelInviteViewModel(String panelInviteHash, AssessmentPanelInviteResource invite, boolean userLoggedIn) {
        this.panelInviteHash = panelInviteHash;
        this.competitionId = invite.getCompetitionId();
        this.competitionName = invite.getCompetitionName();
        this.panelDate = invite.getPanelDate();
        this.userLoggedIn = userLoggedIn;
    }

    public String getPanelInviteHash() {
        return panelInviteHash;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public ZonedDateTime getPanelDate() {
        return panelDate;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PanelInviteViewModel that = (PanelInviteViewModel) o;

        return new EqualsBuilder()
                .append(userLoggedIn, that.userLoggedIn)
                .append(panelInviteHash, that.panelInviteHash)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(panelDate, that.panelDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(panelInviteHash)
                .append(competitionId)
                .append(competitionName)
                .append(panelDate)
                .append(userLoggedIn)
                .toHashCode();
    }
}
