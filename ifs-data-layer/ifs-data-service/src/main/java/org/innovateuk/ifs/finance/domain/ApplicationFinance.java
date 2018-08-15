package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financeFileEntryId", referencedColumnName = "id")
    private FileEntry financeFileEntry;

    public ApplicationFinance() {
        // no-arg constructor
    }

    public ApplicationFinance(Application application, Organisation organisation) {
        super(organisation);
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
                .map(GrantClaimMaximum::getMaximum)
                .orElse(0);
    }

    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
        return isMatchingOrganisationType(grantClaimMaximum)
                && isMatchingOrganisationSize(grantClaimMaximum)
                && isMatchingResearchCategory(grantClaimMaximum);
    }

    private boolean isMatchingOrganisationType(GrantClaimMaximum grantClaimMaximum) {
        return getOrganisation().getOrganisationType().getId().equals(grantClaimMaximum.getOrganisationType().getId());
    }

    private boolean isMatchingOrganisationSize(GrantClaimMaximum grantClaimMaximum) {
        OrganisationSize organisationSize = getOrganisationSize();
        if (organisationSize == null) {
            return grantClaimMaximum.getOrganisationSize() == null;
        }
        return organisationSize == grantClaimMaximum.getOrganisationSize();
    }

    private boolean isMatchingResearchCategory(GrantClaimMaximum grantClaimMaximum) {
        return getApplication().getResearchCategory() != null &&
                grantClaimMaximum.getResearchCategory().getId().equals(getApplication().getResearchCategory().getId());
    }

}
