package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.Set;

/**
 * View model for the application overview
 */
public class ApplicationOverviewViewModel {

    protected static final String TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS = "Investor Partnerships terms and conditions";
    protected static final String TERMS_AND_CONDITIONS_OTHER = "Award terms and conditions";

    private final ProcessRoleResource processRole;
    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final Set<ApplicationOverviewSectionViewModel> sections;

    public ApplicationOverviewViewModel(ProcessRoleResource processRole, CompetitionResource competition, ApplicationResource application, Set<ApplicationOverviewSectionViewModel> sections) {
        this.processRole = processRole;
        this.competition = competition;
        this.application = application;
        this.sections = sections;
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

    public boolean isLead() {
        return processRole.getRole().isLeadApplicant();
    }

    public String getTermsAndConditionsTerminology() {
        if (FundingType.INVESTOR_PARTNERSHIPS == competition.getFundingType()) {
            return TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
        }
        return TERMS_AND_CONDITIONS_OTHER;
    }

}
