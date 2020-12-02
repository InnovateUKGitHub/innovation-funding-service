package org.innovateuk.ifs.management.supporters.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;

public class ManageSupportersViewModel {

    private final Long competitionId;
    private final String competitionName;
    private final FundingType competitionFundingType;
    private final CompetitionStatus competitionStatus;

    public ManageSupportersViewModel(CompetitionResource competition) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.competitionFundingType = competition.getFundingType();
        this.competitionStatus = competition.getCompetitionStatus();
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isAllocateLinkEnabled() {
        return competitionFundingType == FundingType.KTP
                && competitionStatus.isLaterThan(READY_TO_OPEN)
                && !competitionStatus.isLaterThan(IN_ASSESSMENT);
    }
}
