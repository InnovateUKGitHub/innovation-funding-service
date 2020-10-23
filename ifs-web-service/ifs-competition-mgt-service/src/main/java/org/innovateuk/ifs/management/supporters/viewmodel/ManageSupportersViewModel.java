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
    private final boolean supporterEnabled;

    public ManageSupportersViewModel(CompetitionResource competition, boolean supporterEnabled) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.competitionFundingType = competition.getFundingType();
        this.competitionStatus = competition.getCompetitionStatus();
        this.supporterEnabled = supporterEnabled;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isAllocateLinkEnabled() {
        return supporterEnabled
                && competitionFundingType == FundingType.KTP
                && competitionStatus.isLaterThan(READY_TO_OPEN)
                && !competitionStatus.isLaterThan(IN_ASSESSMENT);
    }
}
