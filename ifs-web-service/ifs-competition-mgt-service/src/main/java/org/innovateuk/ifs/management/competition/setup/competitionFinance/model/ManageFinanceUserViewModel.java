package org.innovateuk.ifs.management.competition.setup.competitionFinance.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class ManageFinanceUserViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;
    private List<UserResource> availableCompFinanceUsers;
    private List<UserResource> externalFinanceReviewersAssignedToCompetition;
    private List<UserResource> pendingCompFinanceInvitesForCompetition;
    private String tab;

    public ManageFinanceUserViewModel(Long competitionId, String competitionName,
                                      List<UserResource> availableCompFinanceUsers,
                                      List<UserResource> externalFinanceReviewersAssignedToCompetition,
                                      List<UserResource> pendingCompFinanceInvitesForCompetition,
                                      String tab
    ) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.availableCompFinanceUsers = availableCompFinanceUsers;
        this.externalFinanceReviewersAssignedToCompetition = externalFinanceReviewersAssignedToCompetition;
        this.pendingCompFinanceInvitesForCompetition = pendingCompFinanceInvitesForCompetition;
        this.tab = tab;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<UserResource> getAvailableCompFinanceUsers() {
        return availableCompFinanceUsers;
    }

    public List<UserResource> getExternalFinanceReviewersAssignedToCompetition() {
        return externalFinanceReviewersAssignedToCompetition;
    }

    public List<UserResource> getPendingCompFinanceInvitesForCompetition() {
        return pendingCompFinanceInvitesForCompetition;
    }

    public String getTab() {
        return tab;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageFinanceUserViewModel viewModel = (ManageFinanceUserViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, viewModel.competitionId)
                .append(competitionName, viewModel.competitionName)
                .append(availableCompFinanceUsers, viewModel.availableCompFinanceUsers)
                .append(externalFinanceReviewersAssignedToCompetition, viewModel.externalFinanceReviewersAssignedToCompetition)
                .append(pendingCompFinanceInvitesForCompetition, viewModel.pendingCompFinanceInvitesForCompetition)
                .append(tab, viewModel.tab)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(availableCompFinanceUsers)
                .append(externalFinanceReviewersAssignedToCompetition)
                .append(pendingCompFinanceInvitesForCompetition)
                .append(tab)
                .toHashCode();
    }
}
