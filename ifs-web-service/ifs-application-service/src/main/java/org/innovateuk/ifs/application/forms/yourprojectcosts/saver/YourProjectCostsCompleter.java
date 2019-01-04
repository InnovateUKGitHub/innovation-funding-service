package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
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

    //TODO remove IFS-4982
    private PublicContentItemRestService publicContentItemRestService;

    YourProjectCostsCompleter() {}

    public YourProjectCostsCompleter(SectionService sectionService, OrganisationRestService organisationRestService, ApplicationRestService applicationRestService, CompetitionRestService competitionRestService, PublicContentItemRestService publicContentItemRestService) {
        this.sectionService = sectionService;
        this.organisationRestService = organisationRestService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.publicContentItemRestService = publicContentItemRestService;
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
        handleMarkAcademicFinancesAsNotRequired(organisation.getOrganisationType(), processRole.getApplicationId(), application.getCompetition(), processRole.getId());
        handleMarkProcurementYourFundingAsNotRequired(application.getCompetition(), application.getId(), processRole.getId());
    }

    private void handleMarkProcurementYourFundingAsNotRequired(Long competitionId, Long applicationId, Long processRoleId) {
        PublicContentItemResource publicContent = publicContentItemRestService.getItemByCompetitionId(competitionId).getSuccess();
        if (publicContent.getPublicContentResource().getFundingType() == FundingType.PROCUREMENT) {
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }

    private void handleMarkAcademicFinancesAsNotRequired(long organisationType, long applicationId, long competitionId, long processRoleId) {
        if (OrganisationTypeEnum.RESEARCH.getId() == organisationType
                && !researchUserSeesOrganisationSection(competitionId)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
        }
    }

    private boolean researchUserSeesOrganisationSection(long competitionId) {
        return Boolean.TRUE.equals(competitionRestService.getCompetitionById(competitionId)
                .getSuccess()
                .getIncludeYourOrganisationSection());
    }
}