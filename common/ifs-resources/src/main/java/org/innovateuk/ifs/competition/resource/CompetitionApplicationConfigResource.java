package org.innovateuk.ifs.competition.resource;

import java.math.BigDecimal;

public class CompetitionApplicationConfigResource {

    private BigDecimal maximumFundingSought;
    private boolean imSurveyRequired;

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

    public boolean isImSurveyRequired() {
        return imSurveyRequired;
    }

    public void setImSurveyRequired(boolean imSurveyRequired) {
        this.imSurveyRequired = imSurveyRequired;
    }
}
