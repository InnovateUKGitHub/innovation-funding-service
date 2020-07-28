package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.persistence.*;

@Entity
public class CompetitionFinanceType {

    @EmbeddedId
    private CompetitionFinanceTypeId competitionFinanceTypeId;

//    @MapsId("competitionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionId", referencedColumnName="id", insertable = false, updatable = false)
    private Competition competition;

    private int priority;

    public CompetitionFinanceType(Competition competition, FinanceRowType financeRowType, int priority) {
        this.competitionFinanceTypeId = new CompetitionFinanceTypeId(competition.getId(), financeRowType);
        this.competition = competition;
        this.priority = priority;
    }

    public CompetitionFinanceTypeId getCompetitionFinanceTypeId() {
        return competitionFinanceTypeId;
    }

    public Competition getCompetition() {
        return competition;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
