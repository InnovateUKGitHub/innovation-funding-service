package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

/**
 * Form used to capture "Your organisation" information
 */
public class YourOrganisationForm {

    private OrganisationSize organisationSize;
    private Long turnover;
    private Long headCount;
    private boolean stateAidEligibility;

    public YourOrganisationForm(OrganisationSize organisationSize, Long turnover, Long headCount, boolean stateAidEligibility) {
        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
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

    public Long getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Long headCount) {
        this.headCount = headCount;
    }

    public boolean isStateAidEligibility() {
        return stateAidEligibility;
    }

    public void setStateAidEligibility(boolean stateAidEligibility) {
        this.stateAidEligibility = stateAidEligibility;
    }
}