package org.innovateuk.ifs.application.forms.sections.yourorganisation.populator;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.restservice.YourOrganisationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A populator to build a YourOrganisationViewModel for the "Your organisation" pages.
 */
@Component
public class ApplicationYourOrganisationViewModelPopulator {

    private YourOrganisationRestService yourOrganisationRestService;
    private SectionService sectionService;
    private CompetitionRestService competitionRestService;

    public ApplicationYourOrganisationViewModelPopulator(
            YourOrganisationRestService yourOrganisationRestService,
            SectionService sectionService,
            CompetitionRestService competitionRestService) {

        this.yourOrganisationRestService = yourOrganisationRestService;
        this.sectionService = sectionService;
        this.competitionRestService = competitionRestService;
    }

    public YourOrganisationViewModel populate(long applicationId, long competitionId, long organisationId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        boolean showStateAidAgreement =
                yourOrganisationRestService.isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        List<SectionResource> fundingSections = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES);

        boolean fundingSectionComplete = fundingSections.stream().findAny().map(fundingSection -> {
            List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);
            return completedSectionIds.contains(fundingSection.getId());
        }).orElse(false);

        return new YourOrganisationViewModel(showStateAidAgreement, fundingSectionComplete, competition.isH2020());
    }
}
