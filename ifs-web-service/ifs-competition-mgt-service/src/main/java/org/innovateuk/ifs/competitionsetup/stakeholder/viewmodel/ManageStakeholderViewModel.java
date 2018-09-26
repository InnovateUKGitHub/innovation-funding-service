package org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;

public class ManageStakeholderViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;

    public ManageStakeholderViewModel(Long competitionId, String competitionName) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageStakeholderViewModel that = (ManageStakeholderViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .toHashCode();
    }
}

