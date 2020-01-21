package org.innovateuk.ifs.project.organisationsize.form;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Form used to change organisation size when growth table is not required.
 */

public class ProjectOrganisationSizeWithoutGrowthTableForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    @Max(value = Integer.MAX_VALUE, message = "{validation.standard.integer.max.value.format}")
    private BigDecimal turnover;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    @Max(value = Integer.MAX_VALUE, message = "{validation.standard.integer.max.value.format}")
    private Long headCount;

    public ProjectOrganisationSizeWithoutGrowthTableForm(OrganisationSize organisationSize,
                                                         BigDecimal turnover,
                                                         Long headCount) {
        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public Long getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Long headCount) {
        this.headCount = headCount;
    }
}
