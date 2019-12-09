package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel.YourFinancesRowViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel.YourProjectFinancesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class YourProjectFinancesModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationUrlHelper applicationUrlHelper;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public YourProjectFinancesViewModel populate(long applicationId, long sectionId, long organisationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<Long> completedSections = sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        ApplicationFinanceResource applicationFinanceResource = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        List<YourFinancesRowViewModel> rows = sectionRestService.getChildSectionsByParentId(sectionId).getSuccess()
                .stream()
                .filter(subSection -> !isSectionExcluded(subSection, competition, organisation))
                .map(subSection ->
                        new YourFinancesRowViewModel(subSection.getName(),
                                applicationUrlHelper.getSectionUrl(subSection.getType(), subSection.getId(), applicationId, organisationId, application.getCompetition()).get(),
                                completedSections.contains(subSection.getId()))
                ).collect(toList());
        return new YourProjectFinancesViewModel(applicationId, application.getName(), competition,
                applicationFinanceResource,
                rows);
    }

    private boolean isSectionExcluded(SectionResource section, CompetitionResource competition, OrganisationResource organisation) {
        if (section.getType() == SectionType.ORGANISATION_FINANCES) {
            return competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
        }
        return section.getType() == SectionType.FUNDING_FINANCES && competition.isFullyFunded();
    }
}
