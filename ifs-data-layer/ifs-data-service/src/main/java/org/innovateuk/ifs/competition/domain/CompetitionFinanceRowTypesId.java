package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class CompetitionFinanceRowTypesId implements Serializable {

    private static final long serialVersionUID = 125791623584523765L;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    @Enumerated(EnumType.STRING)
    private FinanceRowType financeRowType;

    public CompetitionFinanceRowTypesId() {
    }

    public CompetitionFinanceRowTypesId(Competition competition, FinanceRowType financeRowType) {
        this.competition = competition;
        this.financeRowType = financeRowType;
    }

    public Competition getCompetitionId() {
        return competition;
    }

    public FinanceRowType getFinanceRowType() {
        return financeRowType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionFinanceRowTypesId that = (CompetitionFinanceRowTypesId) o;

        return new EqualsBuilder()
                .append(competition, that.competition)
                .append(financeRowType, that.financeRowType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competition)
                .append(financeRowType)
                .toHashCode();
    }
}
