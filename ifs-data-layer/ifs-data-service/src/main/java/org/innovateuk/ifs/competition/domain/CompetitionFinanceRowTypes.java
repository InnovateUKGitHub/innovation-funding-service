package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CompetitionFinanceRowTypes implements Serializable {

    private static final long serialVersionUID = -3022167934082129189L;
    @Id
    @Column(name = "competition_id")
    private Long competitionId;

    @Id
    @Enumerated(EnumType.STRING)
    private FinanceRowType financeRowType;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    private int priority;

    CompetitionFinanceRowTypes() {}

    public CompetitionFinanceRowTypes(Competition competition, FinanceRowType financeRowType, int priority) {
        this.competitionId = competition.getId();
        this.financeRowType = financeRowType;
        this.competition = competition;
        this.priority = priority;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public FinanceRowType getFinanceRowType() {
        return financeRowType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionFinanceRowTypes that = (CompetitionFinanceRowTypes) o;

        return new EqualsBuilder()
                .append(priority, that.priority)
                .append(competitionId, that.competitionId)
                .append(financeRowType, that.financeRowType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(financeRowType)
                .append(priority)
                .toHashCode();
    }
}
