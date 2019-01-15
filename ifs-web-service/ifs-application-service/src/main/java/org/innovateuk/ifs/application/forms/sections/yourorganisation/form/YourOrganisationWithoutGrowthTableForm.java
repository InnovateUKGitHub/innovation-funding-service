package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.NotNull;

/**
 * Form used to capture "Your organisation" information when a growth table is required.
 */
public class YourOrganisationWithoutGrowthTableForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long turnover;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long headCount;

    private Boolean stateAidAgreed;

    YourOrganisationWithoutGrowthTableForm(
            OrganisationSize organisationSize,
            Long turnover,
            Long headCount,
            Boolean stateAidAgreed) {

        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
        this.stateAidAgreed = stateAidAgreed;
    }

    YourOrganisationWithoutGrowthTableForm() {
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

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }
}