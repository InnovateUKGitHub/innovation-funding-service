package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.persistence.*;

@Entity
public class CompetitionFinanceRowTypes {

    @EmbeddedId
    private CompetitionFinanceRowTypesId competitionFinanceRowTypesId;

    private int priority;

    CompetitionFinanceRowTypes() {}

    public CompetitionFinanceRowTypes(Competition competition, FinanceRowType financeRowType, int priority) {
        this.competitionFinanceRowTypesId = new CompetitionFinanceRowTypesId(competition, financeRowType);
        this.priority = priority;
    }

    @Transient
    public FinanceRowType getFinanceRowType() {
        return competitionFinanceRowTypesId.getFinanceRowType();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
