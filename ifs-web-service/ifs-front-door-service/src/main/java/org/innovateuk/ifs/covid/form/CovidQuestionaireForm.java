package org.innovateuk.ifs.covid.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class CovidQuestionaireForm {

    private Boolean business;
    private Boolean awardRecipient;
    private Boolean challengeTiming;
    private Boolean challengeCashflow;
    private Boolean challengeLargeFundingGap;
    private Boolean challengeSignificantFundingGap;

    public Boolean getBusiness() {
        return business;
    }

    public void setBusiness(Boolean business) {
        this.business = business;
    }

    public Boolean getAwardRecipient() {
        return awardRecipient;
    }

    public void setAwardRecipient(Boolean awardRecipient) {
        this.awardRecipient = awardRecipient;
    }

    public Boolean getChallengeTiming() {
        return challengeTiming;
    }

    public void setChallengeTiming(Boolean challengeTiming) {
        this.challengeTiming = challengeTiming;
    }

    public Boolean getChallengeCashflow() {
        return challengeCashflow;
    }

    public void setChallengeCashflow(Boolean challengeCashflow) {
        this.challengeCashflow = challengeCashflow;
    }

    public Boolean getChallengeLargeFundingGap() {
        return challengeLargeFundingGap;
    }

    public void setChallengeLargeFundingGap(Boolean challengeLargeFundingGap) {
        this.challengeLargeFundingGap = challengeLargeFundingGap;
    }

    public Boolean getChallengeSignificantFundingGap() {
        return challengeSignificantFundingGap;
    }

    public void setChallengeSignificantFundingGap(Boolean challengeSignificantFundingGap) {
        this.challengeSignificantFundingGap = challengeSignificantFundingGap;
    }

    /* View logic */
    @JsonIgnore
    public boolean isDecisionMade() {
        return FALSE.equals(business)
                || FALSE.equals(awardRecipient)
                || TRUE.equals(challengeTiming)
                || TRUE.equals(challengeCashflow)
                || TRUE.equals(challengeLargeFundingGap)
                || TRUE.equals(challengeSignificantFundingGap);
    }
}
