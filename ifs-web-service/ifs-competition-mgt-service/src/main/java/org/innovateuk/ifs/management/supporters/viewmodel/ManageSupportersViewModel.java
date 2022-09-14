package org.innovateuk.ifs.management.supporters.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;

public class ManageSupportersViewModel {

    private final Long competitionId;
    private final String competitionName;
    private final CompetitionStatus competitionStatus;
    private final boolean ktpCompetition;

    public ManageSupportersViewModel(CompetitionResource competition) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.competitionStatus = competition.getCompetitionStatus();
        this.ktpCompetition = competition.isKtp();
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isAllocateLinkEnabled() {
        return  ktpCompetition
                && competitionStatus.isLaterThan(READY_TO_OPEN)
                && !competitionStatus.isLaterThan(IN_ASSESSMENT);
    }
}
