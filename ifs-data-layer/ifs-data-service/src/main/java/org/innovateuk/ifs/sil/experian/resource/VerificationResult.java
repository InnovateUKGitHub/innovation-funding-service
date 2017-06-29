package org.innovateuk.ifs.sil.experian.resource;

import java.util.List;

public class VerificationResult {
    private String personalDetailsScore;
    private String addressScore;
    private String companyNameScore;
    private String regNumberScore;
    private List<Condition> conditions;

    public VerificationResult() {}

    public VerificationResult(String personalDetailsScore, String addressScore, String companyNameScore, String regNumberScore, List<Condition> conditions) {
        this.personalDetailsScore = personalDetailsScore;
        this.addressScore = addressScore;
        this.companyNameScore = companyNameScore;
        this.regNumberScore = regNumberScore;
        this.conditions = conditions;
    }

    public String getPersonalDetailsScore() {
        return personalDetailsScore;
    }

    public void setPersonalDetailsScore(String personalDetailsScore) {
        this.personalDetailsScore = personalDetailsScore;
    }

    public String getAddressScore() {
        return addressScore;
    }

    public void setAddressScore(String addressScore) {
        this.addressScore = addressScore;
    }

    public String getCompanyNameScore() {
        return companyNameScore;
    }

    public void setCompanyNameScore(String companyNameScore) {
        this.companyNameScore = companyNameScore;
    }

    public String getRegNumberScore() {
        return regNumberScore;
    }

    public void setRegNumberScore(String regNumberScore) {
        this.regNumberScore = regNumberScore;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
