package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.user.resource.OrganisationSize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ApplicationFinanceResource extends BaseFinanceResource {

    private Long financeFileEntry;

    public ApplicationFinanceResource(ApplicationFinanceResource applicationFinance) {

        super(applicationFinance);

        if (applicationFinance != null && applicationFinance.getFinanceFileEntry() != null) {
            this.financeFileEntry = applicationFinance.getFinanceFileEntry();
        }
    }

    // for mapstruct
    public ApplicationFinanceResource() {
    }

    public ApplicationFinanceResource(Long financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public ApplicationFinanceResource(Long id, Long organisation, Long application, OrganisationSize organisationSize, Long financeFileEntry) {
        super(id, organisation, application, organisationSize);
        this.financeFileEntry = financeFileEntry;
    }

    public ApplicationFinanceResource(Long id, Long organisation, Long application, OrganisationSize organisationSize) {
        super(id, organisation, application, organisationSize);
    }

    public Long getFinanceFileEntry() {
        return financeFileEntry;
    }

    public void setFinanceFileEntry(Long financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public Long getApplication() {
        return super.getTarget();
    }

    public void setApplication(Long target) {
        super.setTarget(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationFinanceResource that = (ApplicationFinanceResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(target, that.target)
                .append(financeFileEntry, that.financeFileEntry)
                .append(organisationSize, that.organisationSize)
                .append(financeOrganisationDetails, that.financeOrganisationDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(target)
                .append(financeFileEntry)
                .append(organisationSize)
                .append(financeOrganisationDetails)
                .toHashCode();
    }
}
