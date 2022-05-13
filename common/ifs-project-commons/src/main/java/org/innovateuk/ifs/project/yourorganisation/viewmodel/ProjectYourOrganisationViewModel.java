package org.innovateuk.ifs.project.yourorganisation.viewmodel;

import lombok.Getter;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.ApplicationYourOrganisationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;

@Getter
public class ProjectYourOrganisationViewModel extends ApplicationYourOrganisationViewModel {
    private final long projectId;
    private final String projectName;
    private final long organisationId;
    private final boolean readOnly;
    private final UserResource loggedInUser;
    private final boolean isAllEligibilityAndViabilityInReview;
    private YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel;
    private boolean partnerOrgDisplay;

    public ProjectYourOrganisationViewModel(long applicationId,
                                            CompetitionResource competition,
                                            OrganisationResource organisation,
                                            boolean maximumFundingLevelConstant,
                                            boolean showOrganisationSizeAlert,
                                            long projectId,
                                            String projectName,
                                            boolean readOnly,
                                            UserResource loggedInUser,
                                            boolean isAllEligibilityAndViabilityInReview,
                                            String hash) {
        super(applicationId, competition, organisation.getOrganisationTypeEnum(), maximumFundingLevelConstant, showOrganisationSizeAlert, false, hash);
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisation.getId();
        this.readOnly = readOnly;
        this.loggedInUser = loggedInUser;
        this.isAllEligibilityAndViabilityInReview = isAllEligibilityAndViabilityInReview;
    }

    public boolean isAllowedToEditOrganisationSize() {
        return getLoggedInUser().hasAuthority(Authority.PROJECT_FINANCE) && isAllEligibilityAndViabilityInReview;
    }


    public void setOrgDetailsViewModel(YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel) {
        this.orgDetailsViewModel = orgDetailsViewModel;
    }

    public void setPartnerOrgDisplay(boolean partnerOrgDisplay) {
        this.partnerOrgDisplay = partnerOrgDisplay;
    }
}
