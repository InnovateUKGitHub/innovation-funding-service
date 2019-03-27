package org.innovateuk.ifs.competitionsetup.core.form;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.Funder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Form representing a row in the funders table.
 */
public class FunderRowForm {


    @NotNull(message = "{validation.additionalinfoform.fundername.required}")
    private Funder funder;

    @Min(value=0, message = "{validation.additionalinfoform.funderbudget.min}")
    @NotNull(message = "{validation.additionalinfoform.funderbudget.required}")
    private BigInteger funderBudget;

    @NotNull
    private Boolean coFunder;

    // TODO IFS-3807 the original funder text. This is included so that if there is no corresponding enum then we can
    // TODO show the original value to the user. After IFS-5508 this should be removed.
    private String funderOriginalName;

    public FunderRowForm(){
        // For Spring binding
    }

    public FunderRowForm(CompetitionFunderResource funderResource) {
        // TODO IFS-3807. If there is no corresponding Funder enum to the free text field it is set to null. This has the
        // TODO the effect that the user will be forced to select from the approved list on editing. When IFS-5508 is
        // TODO completed this should no longer be an issue as we will be saving the enum to the database.
        this.setFunder(Funder.fromDisplayName(funderResource.getFunder()));
        this.setFunderOriginalName(funderResource.getFunder());
        this.setFunderBudget(funderResource.getFunderBudget());
        this.setCoFunder(funderResource.getCoFunder());
    }

    public Funder getFunder() {
        return funder;
    }

    public void setFunder(Funder funder) {
        this.funder = funder;
    }

    public String getFunderOriginalName() {
        return funderOriginalName;
    }

    public void setFunderOriginalName(String funderOriginalName) {
        this.funderOriginalName = funderOriginalName;
    }

    public BigInteger getFunderBudget() {
        return funderBudget;
    }

    public void setFunderBudget(BigInteger funderBudget) {
        this.funderBudget = funderBudget;
    }

    public Boolean getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(Boolean coFunder) {
        this.coFunder = coFunder;
    }
}
