package org.innovateuk.ifs.management.competition.setup.postawardservice.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;

public class ChoosePostAwardServiceViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;

    public ChoosePostAwardServiceViewModel(Long competitionId, String competitionName) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}
