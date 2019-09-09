package org.innovateuk.ifs.finance.resource;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ApplicationFinanceResource extends BaseFinanceResource {

    private Long financeFileEntry;
    private Integer maximumFundingLevel;
    private String workPostcode;

    public ApplicationFinanceResource(ApplicationFinanceResource applicationFinance) {

        super(applicationFinance);

        if (applicationFinance != null && applicationFinance.getFinanceFileEntry() != null) {
            this.financeFileEntry = applicationFinance.getFinanceFileEntry();
            this.workPostcode = applicationFinance.getWorkPostcode();
        }
    }

    public ApplicationFinanceResource() {
    }

    public ApplicationFinanceResource(Long financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public ApplicationFinanceResource(long id,
                                      long organisation,
                                      long application,
                                      OrganisationSize organisationSize,
                                      String workPostcode) {
        super(id, organisation, application, organisationSize);
        this.workPostcode = workPostcode;
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

    public String getWorkPostcode() {
        return workPostcode;
    }

    public void setWorkPostcode(String workPostcode) {
        this.workPostcode = workPostcode;
    }
}