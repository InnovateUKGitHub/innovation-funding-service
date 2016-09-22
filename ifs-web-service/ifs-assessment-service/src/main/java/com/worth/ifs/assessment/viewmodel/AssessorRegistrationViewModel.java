package com.worth.ifs.assessment.viewmodel;

import java.util.List;

/**
 * Created by wouter on 15/09/2016.
 */
public class AssessorRegistrationViewModel {

    private String competitionInviteHash;
    private String email;
    private List<String> genderOptions;
    private List<String> ethnicityOptions;
    private List<String> disabilityOptions;

    public AssessorRegistrationViewModel(String competitionInviteHash, String email, List<String> genderOptions, List<String> ethnicityOptions, List<String> disabilityOptions) {
        this.competitionInviteHash = competitionInviteHash;
        this.email = email;
        this.genderOptions = genderOptions;
        this.ethnicityOptions = ethnicityOptions;
        this.disabilityOptions = disabilityOptions;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(List<String> genderOptions) {
        this.genderOptions = genderOptions;
    }

    public List<String> getEthnicityOptions() {
        return ethnicityOptions;
    }

    public void setEthnicityOptions(List<String> ethnicityOptions) {
        this.ethnicityOptions = ethnicityOptions;
    }

    public List<String> getDisabilityOptions() {
        return disabilityOptions;
    }

    public void setDisabilityOptions(List<String> disabilityOptions) {
        this.disabilityOptions = disabilityOptions;
    }
}
