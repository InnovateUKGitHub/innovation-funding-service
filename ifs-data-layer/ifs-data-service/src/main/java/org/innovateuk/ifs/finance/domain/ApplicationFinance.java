package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.Organisation;

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
        return getOrganisation().getOrganisationType().getGrantClaimMaximums().stream()
                .filter(this::isMatchingGrantClaimMaximum)
                .findAny()
                .map(GrantClaimMaximum::getMaximum)
                .orElse(0);
    }

    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
         return isMatchingCompetitionType(grantClaimMaximum)
                && isMatchingOrganisationSize(grantClaimMaximum)
                && isMatchingResearchCategory(grantClaimMaximum);
    }

    private boolean isMatchingCompetitionType(GrantClaimMaximum grantClaimMaximum) {
        return grantClaimMaximum.getCompetitionType().getId().equals(getApplication().getCompetition().getCompetitionType().getId());
    }

    private boolean isMatchingOrganisationSize(GrantClaimMaximum grantClaimMaximum) {
        return (grantClaimMaximum.getOrganisationSize() == null && getOrganisationSize() == null)
                || (getOrganisationSize() != null
                && grantClaimMaximum.getOrganisationSize().getId().equals(getOrganisationSize().getId()));
    }

    private boolean isMatchingResearchCategory(GrantClaimMaximum grantClaimMaximum) {
        return getApplication().getResearchCategory() != null &&
                grantClaimMaximum.getResearchCategory().getId().equals(getApplication().getResearchCategory().getId());
    }

}
