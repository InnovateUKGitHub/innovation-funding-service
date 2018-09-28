package org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class ManageStakeholderViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;
    private List<UserResource> availableStakeholders;
    private List<UserResource> stakeholdersAssignedToCompetition;

    public ManageStakeholderViewModel(Long competitionId, String competitionName,
                                      List<UserResource> availableStakeholders,
                                      List<UserResource> stakeholdersAssignedToCompetition
                                      ) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.availableStakeholders = availableStakeholders;
        this.stakeholdersAssignedToCompetition = stakeholdersAssignedToCompetition;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<UserResource> getAvailableStakeholders() {
        return availableStakeholders;
    }

    public List<UserResource> getStakeholdersAssignedToCompetition() {
        return stakeholdersAssignedToCompetition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageStakeholderViewModel viewModel = (ManageStakeholderViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, viewModel.competitionId)
                .append(competitionName, viewModel.competitionName)
                .append(availableStakeholders, viewModel.availableStakeholders)
                .append(stakeholdersAssignedToCompetition, viewModel.stakeholdersAssignedToCompetition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(availableStakeholders)
                .append(stakeholdersAssignedToCompetition)
                .toHashCode();
    }
}

