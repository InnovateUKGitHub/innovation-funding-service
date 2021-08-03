package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.innovateuk.ifs.util.TermsAndConditionsUtil.TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
import static org.innovateuk.ifs.util.TermsAndConditionsUtil.TERMS_AND_CONDITIONS_OTHER;

/**
 * View model for the application overview
 */
public class ApplicationOverviewViewModel implements BaseAnalyticsViewModel {

    private final ProcessRoleResource processRole;
    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final Set<ApplicationOverviewSectionViewModel> sections;
    private final Boolean reopened;
    private final ZonedDateTime reopenedDate;
    private final boolean thirdPartyProcurement;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;

    public ApplicationOverviewViewModel(ProcessRoleResource processRole, CompetitionResource competition,
                                        ApplicationResource application, Set<ApplicationOverviewSectionViewModel> sections,
                                        Boolean reopened, ZonedDateTime reopenedDate,
                                        CompetitionThirdPartyConfigResource thirdPartyConfig) {
        this.processRole = processRole;
        this.competition = competition;
        this.application = application;
        this.sections = sections;
        this.reopened = reopened;
        this.reopenedDate = reopenedDate;
        this.thirdPartyProcurement = competition.getTermsAndConditions().isProcurementThirdParty();
        this.thirdPartyConfig = thirdPartyConfig;
    }

    @Override
    public Long getApplicationId() {
        return application.getId();
    }

    @Override
    public String getCompetitionName() {
        return competition.getName();
    }

    public ProcessRoleResource getProcessRole() {
        return processRole;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public Set<ApplicationOverviewSectionViewModel> getSections() {
        return sections;
    }

    public boolean isThirdPartyProcurement() { return thirdPartyProcurement; }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() { return thirdPartyConfig; }

    public boolean isLead() {
        return processRole.getRole().isLeadApplicant();
    }

    public String getTermsAndConditionsTerminology() {
        if (FundingType.INVESTOR_PARTNERSHIPS == competition.getFundingType()) {
            return TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
        }
        return TERMS_AND_CONDITIONS_OTHER;
    }

    public Boolean getReopened() {
        return reopened;
    }

    public ZonedDateTime getReopenedDate() {
        return reopenedDate;
    }
}
