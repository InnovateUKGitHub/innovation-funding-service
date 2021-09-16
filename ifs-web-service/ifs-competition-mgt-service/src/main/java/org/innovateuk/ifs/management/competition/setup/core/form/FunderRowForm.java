package org.innovateuk.ifs.management.competition.setup.core.form;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.Funder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Objects;

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

    public FunderRowForm(){
    }

    public FunderRowForm(CompetitionFunderResource funderResource) {
        this.setFunder(funderResource.getFunder());
        this.setFunderBudget(funderResource.getFunderBudget());
        this.setCoFunder(funderResource.getCoFunder());
    }

    public Funder getFunder() {
        return funder;
    }

    public void setFunder(Funder funder) {
        this.funder = funder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunderRowForm)) return false;
        FunderRowForm that = (FunderRowForm) o;
        return funder == that.funder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(funder);
    }
}
