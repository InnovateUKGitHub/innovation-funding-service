package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CovidType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD_WITH_VAT;

@Component
public class YourProjectCostsViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Value("${ifs.ktp.fec.finance.model.enabled}")
    private boolean fecFinanceModelEnabled;

    public YourProjectCostsViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        boolean complete = completedSectionIds.contains(sectionId);

        boolean includeVat = STANDARD_WITH_VAT.equals(competition.getApplicationFinanceType());

        long yourFundingSectionId = getYourFundingSectionId(section);
        boolean yourFundingRequired = !completedSectionIds.contains(yourFundingSectionId);
        long yourFecCostSectionId = getYourFecCostSectionId(section);
        boolean yourFecCostRequired = !completedSectionIds.contains(yourFecCostSectionId);
        boolean projectCostSectionLocked = isProjectCostSectionLocked(section, open, yourFundingRequired, yourFecCostRequired);

        return new YourProjectCostsViewModel(applicationId,
                competition.getName(),
                sectionId,
                competition.getId(),
                organisationId,
                complete,
                open,
                includeVat,
                application.getName(),
                organisation.getName(),
                getYourFinancesUrl(applicationId, organisationId),
                FundingType.PROCUREMENT == competition.getFundingType(),
                FundingType.KTP == competition.getFundingType(),
                competition.getFinanceRowTypes(),
                competition.isOverheadsAlwaysTwenty(),
                CovidType.ADDITIONAL_FUNDING.equals(competition.getCovidType()),
                organisation.getOrganisationType().equals(OrganisationTypeEnum.KNOWLEDGE_BASE.getId()),
                projectCostSectionLocked,
                yourFundingRequired,
                yourFundingSectionId,
                yourFecCostRequired,
                yourFecCostSectionId);
    }

    private String getYourFinancesUrl(long applicationId, long organisationId) {
        return String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId);
    }

    private long getYourFundingSectionId(ApplicantSectionResource section) {
        SectionResource yourFundingSection = sectionService.getFundingFinanceSection(section.getCompetition().getId());
        return yourFundingSection.getId();
    }

    private long getYourFecCostSectionId(ApplicantSectionResource section) {
        SectionResource yourFecCostSection = sectionService.getFecCostFinanceSection(section.getCompetition().getId());
        return yourFecCostSection.getId();
    }

    private boolean competitionIsKtp(ApplicantSectionResource section) {
        return KTP.equals(section.getCompetition().getFundingType());
    }

    private boolean isOrganisationTypeKnowledgeBase(ApplicantSectionResource section) {
        return section.getCurrentApplicant().getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.KNOWLEDGE_BASE.getId());
    }

    private boolean isProjectCostSectionLocked(ApplicantSectionResource section, boolean open, boolean yourFundingRequired, boolean yourFecCostRequired) {
        boolean fieldsRequired = yourFundingRequired || yourFecCostRequired;
        return fecFinanceModelEnabled && fieldsRequired && competitionIsKtp(section) && open && isOrganisationTypeKnowledgeBase(section);
    }
}
