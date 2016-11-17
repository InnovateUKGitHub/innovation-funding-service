package com.worth.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.domain.Organisation;

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
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="financeFileEntryId", referencedColumnName="id")
    private FileEntry financeFileEntry;

    public ApplicationFinance() {
    	// no-arg constructor
    }

    public ApplicationFinance(Application application, Organisation organisation) {
        super(organisation);
        this.application = application;
    }

    public ApplicationFinance(long id, Application application, Organisation organisation) {
        super(id, organisation);
        this.application = application;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public FileEntry getFinanceFileEntry() {
        return financeFileEntry;
    }

    public void setFinanceFileEntry(FileEntry financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public void merge(ApplicationFinanceResource applicationFinance) {
        this.setOrganisationSize(applicationFinance.getOrganisationSize());
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
