package org.innovateuk.ifs.application.forms.sections.yourfinances.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesRowViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class YourFinancesModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationUrlHelper applicationUrlHelper;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    public YourFinancesViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        SectionResource yourFinances = sectionRestService.getById(sectionId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<Long> completedSections = sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        List<YourFinancesRowViewModel> rows = yourFinances.getChildSections().stream()
                .map(sectionRestService::getById)
                .map(RestResult::getSuccess)
                .filter(subSection -> !isSectionExcluded(subSection, competition, organisation))
                .map(subSection ->
                        new YourFinancesRowViewModel(subSection.getName(),
                                applicationUrlHelper.getSectionUrl(subSection.getType(), subSection.getId(), applicationId, organisationId, application.getCompetition()).get(),
                                completedSections.contains(subSection.getId()))
                ).collect(toList());
        return new YourFinancesViewModel(applicationId, application.getName(), competition,
                applicationFinanceSummaryViewModelPopulator.populate(applicationId, user),
                rows);
    }

    private boolean isSectionExcluded(SectionResource section, CompetitionResource competition, OrganisationResource organisation) {
        if (section.getType() == SectionType.ORGANISATION_FINANCES) {
            boolean isResearchOrganisation = organisation != null && OrganisationTypeEnum.RESEARCH.getId() == organisation.getOrganisationType();
            boolean excludeYourOrganisationSectionForResearchOrgs =
                    Boolean.FALSE.equals(competition.getIncludeYourOrganisationSection());
            return isResearchOrganisation && excludeYourOrganisationSectionForResearchOrgs;
        }
        return section.getType() == SectionType.FUNDING_FINANCES && competition.isFullyFunded();
    }
}
