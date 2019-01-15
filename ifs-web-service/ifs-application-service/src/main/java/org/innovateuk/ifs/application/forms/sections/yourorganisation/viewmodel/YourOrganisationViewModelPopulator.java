package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A populator to build a YourOrganisationViewModel for the "Your organisation" pages.
 */
@Component
public class YourOrganisationViewModelPopulator {

    private YourOrganisationRestService yourOrganisationRestService;
    private SectionService sectionService;

    public YourOrganisationViewModelPopulator(
            YourOrganisationRestService yourOrganisationRestService,
            SectionService sectionService) {

        this.yourOrganisationRestService = yourOrganisationRestService;
        this.sectionService = sectionService;
    }

    public YourOrganisationViewModel populate(long applicationId, long competitionId, long organisationId) {

        boolean showStateAidAgreement =
                yourOrganisationRestService.isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        List<SectionResource> fundingSections = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES);

        boolean fundingSectionComplete = fundingSections.stream().findAny().map(fundingSection -> {
            List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);
            return completedSectionIds.contains(fundingSection.getId());
        }).orElse(false);

        return new YourOrganisationViewModel(showStateAidAgreement, fundingSectionComplete);
    }
}
