package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

/**
 * Form used to capture "Your organisation" information
 */
public class YourOrganisationForm {

    private OrganisationSize organisationSize;
    private Long turnover;
    private Long headcount;
    private boolean stateAidEligibility;

    public YourOrganisationForm(OrganisationSize organisationSize, Long turnover, Long headcount, boolean stateAidEligibility) {
        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headcount = headcount;
        this.stateAidEligibility = stateAidEligibility;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Long getHeadcount() {
        return headcount;
    }

    public void setHeadcount(Long headcount) {
        this.headcount = headcount;
    }

    public boolean isStateAidEligibility() {
        return stateAidEligibility;
    }

    public void setStateAidEligibility(boolean stateAidEligibility) {
        this.stateAidEligibility = stateAidEligibility;
    }
}