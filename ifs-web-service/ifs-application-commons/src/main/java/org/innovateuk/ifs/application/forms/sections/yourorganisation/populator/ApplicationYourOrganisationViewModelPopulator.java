package org.innovateuk.ifs.application.forms.sections.yourorganisation.populator;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.ApplicationYourOrganisationViewModel;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A populator to build a YourOrganisationViewModel for the "Your organisation" pages.
 */
@Component
public class ApplicationYourOrganisationViewModelPopulator {

    @Autowired
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    public ApplicationYourOrganisationViewModel populate(long applicationId, long competitionId, long organisationId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        List<SectionResource> fundingSections = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES);

        boolean isMaximumFundingLevelConstant = competition.isMaximumFundingLevelConstant(
                organisation::getOrganisationTypeEnum,
                () -> grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()).getSuccess());

        boolean showOrganisationSizeAlert = false;
        if (!isMaximumFundingLevelConstant) {
            boolean fundingSectionComplete = fundingSections.stream().findAny().map(fundingSection -> {
                List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);
                return completedSectionIds.contains(fundingSection.getId());
            }).orElse(false);
            showOrganisationSizeAlert = fundingSectionComplete;
        }

        return new ApplicationYourOrganisationViewModel(applicationId, competition, organisation.getOrganisationTypeEnum(), isMaximumFundingLevelConstant, showOrganisationSizeAlert, false);
    }
}
