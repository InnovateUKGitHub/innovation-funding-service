package org.innovateuk.ifs.competition.resource;

import java.math.BigDecimal;

public class CompetitionApplicationConfigResource {

    private BigDecimal maximumFundingSought;
    private boolean maximumFundingSoughtEnabled;

    public CompetitionApplicationConfigResource() {
    }

    public CompetitionApplicationConfigResource(BigDecimal maximumFundingSought) {
        this.maximumFundingSought = maximumFundingSought;
    }

    public BigDecimal getMaximumFundingSought() {
        return maximumFundingSought;
    }

    public void setMaximumFundingSought(BigDecimal maximumFundingSought) {
        this.maximumFundingSought = maximumFundingSought;
    }

    public boolean isMaximumFundingSoughtEnabled() {
        return maximumFundingSoughtEnabled;
    }

    public void setMaximumFundingSoughtEnabled(boolean maximumFundingSoughtEnabled) {
        this.maximumFundingSoughtEnabled = maximumFundingSoughtEnabled;
    }
}
