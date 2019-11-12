package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.persistence.*;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * ApplicationFinance defines database relations and a model to use client side and server side.
 */
@Entity
public class ApplicationFinance extends Finance {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    private String workPostcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financeFileEntryId", referencedColumnName = "id")
    private FileEntry financeFileEntry;

    public ApplicationFinance() {
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
        if (!isBusinessOrganisationType()) {
            return FundingLevel.HUNDRED.getPercentage();
        }

        if (isMaximumFundingLevelOverridden()) {
            // The same maximum funding level is set for all GrantClaimMaximums when overriding
            return getCompetition().getGrantClaimMaximums().stream().findAny().map(GrantClaimMaximum::getMaximum).get();
        }

        return getCompetition().getGrantClaimMaximums()
                .stream()
                .filter(this::isMatchingGrantClaimMaximum)
                .findFirst()
                .map(GrantClaimMaximum::getMaximum)
                .orElse(0);
    }

    @Override
    public Competition getCompetition() {
        return getApplication().getCompetition();
    }

    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
        return isMatchingResearchCategory(grantClaimMaximum) && isMatchingOrganisationSize(grantClaimMaximum);
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

    private boolean isBusinessOrganisationType() {
        return getOrganisation().getOrganisationType().getId().equals(OrganisationTypeEnum.BUSINESS.getId());
    }

    private boolean isMaximumFundingLevelOverridden() {
        Set<Long> competitionGrantClaimMaximumIds = getCompetition().getGrantClaimMaximums().stream()
                .map(GrantClaimMaximum::getId)
                .collect(toSet());
        Set<Long> templateGrantClaimMaximumIds = getCompetition().getCompetitionType().getTemplate()
                .getGrantClaimMaximums().stream().map(GrantClaimMaximum::getId).collect(toSet());
        return !competitionGrantClaimMaximumIds.equals(templateGrantClaimMaximumIds);
    }

    public String getWorkPostcode() {
        return workPostcode;
    }

    public void setWorkPostcode(String workPostcode) {
        this.workPostcode = workPostcode;
    }
}