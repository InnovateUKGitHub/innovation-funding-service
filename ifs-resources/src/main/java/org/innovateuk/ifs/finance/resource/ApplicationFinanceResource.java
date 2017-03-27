package org.innovateuk.ifs.finance.resource;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ApplicationFinanceResource extends BaseFinanceResource {

    private Long financeFileEntry;
    private Integer maximumFundingLevel;

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

    public ApplicationFinanceResource(Long id, Long organisation, Long application, Long organisationSize, Long financeFileEntry) {
        super(id, organisation, application, organisationSize);
        this.financeFileEntry = financeFileEntry;
    }

    public ApplicationFinanceResource(Long id, Long organisation, Long application, Long organisationSize) {
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

    public Integer getMaximumFundingLevel() {
        return maximumFundingLevel;
    }

    public void setMaximumFundingLevel(Integer maximumFundingLevel) {
        this.maximumFundingLevel = maximumFundingLevel;
    }
}
