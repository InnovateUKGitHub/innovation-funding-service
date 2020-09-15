package org.innovateuk.ifs.finance.resource;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ApplicationFinanceResource extends BaseFinanceResource {

    private Long financeFileEntry;
    private String workPostcode;
    private String internationalLocation;

    public ApplicationFinanceResource(ApplicationFinanceResource applicationFinance) {

        super(applicationFinance);

        if (applicationFinance != null && applicationFinance.getFinanceFileEntry() != null) {
            this.financeFileEntry = applicationFinance.getFinanceFileEntry();
            this.workPostcode = applicationFinance.getWorkPostcode();
            this.internationalLocation = applicationFinance.getInternationalLocation();
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
                                      String workPostcode,
                                      String internationalLocation) {
        super(id, organisation, application, organisationSize);
        this.workPostcode = workPostcode;
        this.internationalLocation = internationalLocation;
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

    public String getWorkPostcode() {
        return workPostcode;
    }

    public void setWorkPostcode(String workPostcode) {
        this.workPostcode = workPostcode;
    }

    public String getInternationalLocation() {
        return internationalLocation;
    }

    public void setInternationalLocation(String internationalLocation) {
        this.internationalLocation = internationalLocation;
    }
}