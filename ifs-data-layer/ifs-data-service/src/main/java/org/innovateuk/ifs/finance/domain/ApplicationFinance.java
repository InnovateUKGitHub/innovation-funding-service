package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.organisation.domain.Organisation;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * ApplicationFinance defines database relations and a model to use client side and server side.
 */
@Entity
public class ApplicationFinance extends Finance {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    private String workPostcode;
    private String internationalLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financeFileEntryId", referencedColumnName = "id")
    private FileEntry financeFileEntry;

    public ApplicationFinance() {
    }

    public ApplicationFinance(Application application, Organisation organisation) {
        super(organisation);
        this.application = application;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    public FileEntry getFinanceFileEntry() {
        return financeFileEntry;
    }

    public void setFinanceFileEntry(FileEntry financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public void setApplication(Application application) {
        this.application = application;
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