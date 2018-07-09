package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public void setApplication(Application application) {
        this.application = application;
    }

    public Integer getMaximumFundingLevel() {
        return getApplication().getCompetition().getGrantClaimMaximums()
                .stream()
                .filter(this::isMatchingGrantClaimMaximum)
                .findAny()
                .map(this::getMaximum).orElse(0);
    }

    private Integer getMaximum(GrantClaimMaximum grantClaimMaximum) {
        switch (getOrganisationSize()) {
            case SMALL:
                return grantClaimMaximum.getSmall();
            case MEDIUM:
                return grantClaimMaximum.getMedium();
            case LARGE:
                return grantClaimMaximum.getLarge();
            default:
                return grantClaimMaximum.getDef();
        }
    }

    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
         return isMatchingOrganisationType(grantClaimMaximum)
                && isMatchingResearchCategory(grantClaimMaximum);
    }

    private boolean isMatchingOrganisationType(GrantClaimMaximum grantClaimMaximum) {
        return getOrganisation().getOrganisationType().getId().equals(grantClaimMaximum.getOrganisationType().getId());
    }

    private boolean isMatchingResearchCategory(GrantClaimMaximum grantClaimMaximum) {
        return getApplication().getResearchCategory() != null &&
                grantClaimMaximum.getResearchCategory().getId().equals(getApplication().getResearchCategory().getId());
    }

}
