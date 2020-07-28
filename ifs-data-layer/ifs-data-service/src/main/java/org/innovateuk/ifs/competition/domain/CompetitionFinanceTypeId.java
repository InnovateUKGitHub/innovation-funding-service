package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class CompetitionFinanceTypeId implements Serializable {

    private static final long serialVersionUID = -8644984031279927011L;
    private Long competitionId;

    @Enumerated(EnumType.STRING)
    private FinanceRowType financeRowType;

    CompetitionFinanceTypeId() {}
    
    public CompetitionFinanceTypeId(Long competitionId, FinanceRowType financeRowType) {
        this.competitionId = competitionId;
        this.financeRowType = financeRowType;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public FinanceRowType getFinanceRowType() {
        return financeRowType;
    }

    public void setFinanceRowType(FinanceRowType financeRowType) {
        this.financeRowType = financeRowType;
    }
}
