package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When the project costs section is complete we need to mark other finance sections that are conditionally not required.
 */
@Service
public class YourProjectCostsCompleter {

    private SectionService sectionService;

    private OrganisationRestService organisationRestService;

    private ApplicationRestService applicationRestService;

    private CompetitionRestService competitionRestService;

    YourProjectCostsCompleter() {}

    @Autowired
    public YourProjectCostsCompleter(SectionService sectionService, OrganisationRestService organisationRestService, ApplicationRestService applicationRestService, CompetitionRestService competitionRestService) {
        this.sectionService = sectionService;
        this.organisationRestService = organisationRestService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
    }

    public ValidationMessages markAsComplete(long sectionId, long applicationId, ProcessRoleResource role) {
        ValidationMessages messages = new ValidationMessages();
        sectionService.markAsComplete(sectionId, applicationId, role.getId()).forEach(messages::addAll);
        if (!messages.hasErrors()) {
            handleMarkProjectCostsAsComplete(role);
        }
        return messages;
    }

    private void handleMarkProjectCostsAsComplete(ProcessRoleResource processRole) {
        ApplicationResource application =  applicationRestService.getApplicationById(processRole.getApplicationId()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(processRole.getOrganisationId()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        handleMarkAcademicFinancesAsNotRequired(organisation.getOrganisationType(), processRole.getApplicationId(), processRole.getId(), competition);
        handleMarkProcurementYourFundingAsNotRequired(application.getCompetition(), application.getId(), processRole.getId(), competition);
    }

    private void handleMarkProcurementYourFundingAsNotRequired(long competitionId, long applicationId, long processRoleId, CompetitionResource competition) {
        if (competition.getFundingType() == FundingType.PROCUREMENT) {
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }

    private void handleMarkAcademicFinancesAsNotRequired(long organisationType, long applicationId, long processRoleId, CompetitionResource competition) {
        if (OrganisationTypeEnum.RESEARCH.getId() == organisationType
                && !researchUserSeesOrganisationSection(competition)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.ORGANISATION_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
        }
    }

    private boolean researchUserSeesOrganisationSection(CompetitionResource competition) {
        return Boolean.TRUE.equals(competition.getIncludeYourOrganisationSection());
    }
}