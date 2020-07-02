package org.innovateuk.ifs.application.forms.sections.yourorganisation.populator;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
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
    private YourOrganisationRestService yourOrganisationRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    public YourOrganisationViewModel populate(long applicationId, long competitionId, long organisationId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        boolean showStateAidAgreement =
                yourOrganisationRestService.isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        List<SectionResource> fundingSections = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES);

        boolean isMaximumFundingLevelConstant = competition.isMaximumFundingLevelConstant(
                () -> organisationRestService.getOrganisationById(organisationId).getSuccess().getOrganisationTypeEnum(),
                () -> grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competition.getId()).getSuccess());

        boolean showOrganisationSizeAlert = false;
        if (!isMaximumFundingLevelConstant) {
            boolean fundingSectionComplete = fundingSections.stream().findAny().map(fundingSection -> {
                List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);
                return completedSectionIds.contains(fundingSection.getId());
            }).orElse(false);
            showOrganisationSizeAlert = fundingSectionComplete;
        }

        return new YourOrganisationViewModel(applicationId, competition.getName(), showStateAidAgreement, showOrganisationSizeAlert, competition.isH2020(), competition.isProcurement());
    }
}
