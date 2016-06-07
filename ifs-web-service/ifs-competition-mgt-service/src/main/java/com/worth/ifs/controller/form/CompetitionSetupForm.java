package com.worth.ifs.controller.form;


/**
 * Generic form class to pass and save section data.
 */
public class CompetitionSetupForm {
    private String actionUrl;
    private Long competitonSetup;

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public Long getCompetitonSetup() {
        return competitonSetup;
    }

    public void setCompetitonSetup(Long competitonSetup) {
        this.competitonSetup = competitonSetup;
    }
}
