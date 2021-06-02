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
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Value("${ifs.ktp.fec.finance.model.enabled}")
    private boolean fecFinanceModelEnabled;

    public YourProjectCostsViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);
        BaseFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        boolean complete = completedSectionIds.contains(sectionId);

        boolean includeVat = STANDARD_WITH_VAT.equals(competition.getApplicationFinanceType());

        if (isUserCanEditFecFinance(competition, section, open)) {
            return getYourFecProjectCostsViewModel(application, competition, organisation, section, finance, completedSectionIds, open, complete, includeVat);
        } else {
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
                    getFinanceRowTypes(competition, finance),
                    competition.isOverheadsAlwaysTwenty(),
                    CovidType.ADDITIONAL_FUNDING.equals(competition.getCovidType()),
                    organisation.getOrganisationType().equals(OrganisationTypeEnum.KNOWLEDGE_BASE.getId()),
                    finance.getFecModelEnabled(),
                    getGrantClaimPercentage(application.getId(), organisation.getId()));
        }
    }

    private boolean isUserCanEditFecFinance(CompetitionResource competition, ApplicantSectionResource section, boolean open) {
        return competition.isKtp()
                && fecFinanceModelEnabled
                && open
                && isOrganisationTypeKnowledgeBase(section);
    }

    private YourProjectCostsViewModel getYourFecProjectCostsViewModel(ApplicationResource application, CompetitionResource competition,
                                                                      OrganisationResource organisation, ApplicantSectionResource section,
                                                                      BaseFinanceResource finance, List<Long> completedSectionIds,
                                                                      boolean open, boolean complete, boolean includeVat) {
        Long yourFundingSectionId = getYourFundingSectionId(section);
        boolean yourFundingRequired = !completedSectionIds.contains(yourFundingSectionId);
        Long yourFecCostSectionId = getYourFecCostSectionId(section);
        boolean yourFecCostRequired = fecFinanceModelEnabled ? isYourFecCostRequired(completedSectionIds, yourFecCostSectionId) : false;
        boolean projectCostSectionLocked = isProjectCostSectionLocked(yourFundingRequired, yourFecCostRequired);

        return new YourProjectCostsViewModel(application.getId(),
                competition.getName(),
                section.getSection().getId(),
                competition.getId(),
                organisation.getId(),
                complete,
                open,
                includeVat,
                application.getName(),
                organisation.getName(),
                getYourFinancesUrl(application.getId(), organisation.getId()),
                FundingType.PROCUREMENT == competition.getFundingType(),
                FundingType.KTP == competition.getFundingType(),
                getFinanceRowTypes(competition, finance),
                competition.isOverheadsAlwaysTwenty(),
                CovidType.ADDITIONAL_FUNDING.equals(competition.getCovidType()),
                organisation.getOrganisationType().equals(OrganisationTypeEnum.KNOWLEDGE_BASE.getId()),
                projectCostSectionLocked,
                yourFundingRequired,
                yourFundingSectionId,
                yourFecCostRequired,
                yourFecCostSectionId,
                finance.getFecModelEnabled(),
                getGrantClaimPercentage(application.getId(), organisation.getId()));
    }

    private boolean isYourFecCostRequired(List<Long> completedSectionIds, Long yourFecCostSectionId) {
        return yourFecCostSectionId != null
                && !completedSectionIds.contains(yourFecCostSectionId);
    }

    private List<FinanceRowType> getFinanceRowTypes(CompetitionResource competition, BaseFinanceResource finance) {
        List<FinanceRowType> costTypes = competition.getFinanceRowTypes();

        if (competition.isKtp()) {
            costTypes = competition.getFinanceRowTypesByFinance(Optional.of(finance));
        }

        return costTypes;
    }

    private BigDecimal getGrantClaimPercentage(long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        return applicationFinance.getGrantClaimPercentage();
    }

    private String getYourFinancesUrl(long applicationId, long organisationId) {
        return String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId);
    }

    private Long getYourFundingSectionId(ApplicantSectionResource section) {
        SectionResource yourFundingSection = sectionService.getFundingFinanceSection(section.getCompetition().getId());
        return yourFundingSection.getId();
    }

    private Long getYourFecCostSectionId(ApplicantSectionResource section) {
        return Optional.ofNullable(sectionService.getFecCostFinanceSection(section.getCompetition().getId()))
                .map(SectionResource::getId)
                .orElse(null);
    }

    private boolean isOrganisationTypeKnowledgeBase(ApplicantSectionResource section) {
        return section.getCurrentApplicant().getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.KNOWLEDGE_BASE.getId());
    }

    private boolean isProjectCostSectionLocked(boolean yourFundingRequired, boolean yourFecCostRequired) {
        return yourFundingRequired || yourFecCostRequired;
    }
}
