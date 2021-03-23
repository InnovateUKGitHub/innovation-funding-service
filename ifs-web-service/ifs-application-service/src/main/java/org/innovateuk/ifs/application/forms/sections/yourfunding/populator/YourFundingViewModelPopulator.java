package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel.ManagementYourFundingViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;

@Component
public class YourFundingViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    public YourFundingViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        if (userCanEdit) {
            return populateApplicant(applicationId, sectionId, organisationId, user);
        }
        return populateManagement(applicationId, sectionId, organisationId, user);
    }

    private YourFundingViewModel populateApplicant(long applicationId, long sectionId, long organisationId, UserResource user) {

        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);
        if (!section.getCurrentApplicant().getOrganisation().getId().equals(organisationId)) {
            return populateManagement(applicationId, sectionId, organisationId, user);
        }
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section
                .getCurrentApplicant().getOrganisation().getId());
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getApplicationFinance(applicationId, section.getCurrentApplicant().getOrganisation().getId()).getSuccess();

        boolean complete = section.isComplete(section.getCurrentApplicant());
        boolean open = section.getApplication().isOpen() &&
                section.getCompetition().isOpen();

        Long subsidyBasisQuestionId = getSubsidyBasisQuestionId(section);
        boolean subsidyBasisQuestionRequired = isSubsidyBasisQuestionRequired(section, subsidyBasisQuestionId);
        Long researchCategoryQuestionId = getResearchCategoryQuestionId(section);
        boolean researchCategoryRequired = isResearchCategoryRequired(section, researchCategoryQuestionId);
        long yourOrganisationSectionId = getYourOrganisationSectionId(section);
        boolean yourOrganisationRequired = !completedSectionIds.contains(yourOrganisationSectionId);
        boolean fundingSectionLocked = isFundingSectionLocked(section, researchCategoryRequired,
                yourOrganisationRequired, subsidyBasisQuestionRequired);
        boolean overridingFundingRules = isMaximumFundingLevelOverridden(section);

        return new YourFundingViewModel(applicationId,
                section.getCompetition().getName(),
                section.getSection().getId(),
                section.getCurrentApplicant().getOrganisation().getId(),
                section.getCompetition().getId(),
                complete,
                open,
                section.getCurrentApplicant().isLead(),
                section.getCurrentApplicant().getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.BUSINESS.getId()),
                section.getApplication().getName(),
                fundingSectionLocked,
                researchCategoryRequired,
                yourOrganisationRequired,
                subsidyBasisQuestionRequired,
                researchCategoryQuestionId,
                yourOrganisationSectionId,
                subsidyBasisQuestionId,
                applicationFinance.getMaximumFundingLevel(),
                format("/application/%d/form/FINANCE/%d", applicationId, section.getCurrentApplicant().getOrganisation().getId()),
                overridingFundingRules,
                section.getCompetition().getFundingType(),
                section.getCurrentApplicant().getOrganisation().getOrganisationTypeEnum());
    }


    private ManagementYourFundingViewModel populateManagement(long applicationId, long sectionId, long organisationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        return new ManagementYourFundingViewModel(applicationId, application.getCompetitionName(), sectionId, organisationId, application.getCompetition(), application.getName(),
                format("/application/%d/form/FINANCE/%d", applicationId, organisationId), competition.getFundingType(), organisation.getOrganisationTypeEnum());
    }

    private Long getSubsidyBasisQuestionId(ApplicantSectionResource section) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(),
                SUBSIDY_BASIS).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private boolean isSubsidyBasisQuestionRequired(ApplicantSectionResource section, Long subsidyBasisQuestionId) {
        return subsidyBasisQuestionId != null && !isQuestionComplete(section, subsidyBasisQuestionId);
    }

    private Long getResearchCategoryQuestionId(ApplicantSectionResource section) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(),
                RESEARCH_CATEGORY).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private boolean isResearchCategoryRequired(ApplicantSectionResource section, Long researchCategoryQuestionId) {
        return researchCategoryQuestionId != null && !isQuestionComplete(section, researchCategoryQuestionId);
    }

    private boolean isQuestionComplete(ApplicantSectionResource section, long questionId) {
        long applicationId = section.getApplication().getId();
        long applicantOrganisationId = section.getCurrentApplicant().getOrganisation().getId();

        return questionIsComplete(applicationId, applicantOrganisationId, questionId);
    }

    private boolean questionIsComplete(long applicationId, long organisationId, long questionId) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService
                .getQuestionStatusesForApplicationAndOrganisation(applicationId, organisationId);
        QuestionStatusResource questionStatus = questionStatuses.get(questionId);
        return questionStatus != null && questionStatus.getMarkedAsComplete();
    }

    private long getYourOrganisationSectionId(ApplicantSectionResource section) {
        SectionResource yourOrganisationSection = sectionService.getOrganisationFinanceSection(
                section.getCompetition().getId());
        return yourOrganisationSection.getId();
    }

    private boolean isFundingSectionLocked(ApplicantSectionResource section,
                                           boolean researchCategoryRequired,
                                           boolean yourOrganisationRequired,
                                           boolean subsidyBasisQuestionRequired) {
        boolean fieldsRequired = researchCategoryRequired || yourOrganisationRequired || subsidyBasisQuestionRequired;
        return fieldsRequired && !competitionIsKtp(section) && !competitionIsLoanType(section)
                && isCompetitionOpen(section) && isOrganisationTypeBusiness(section) &&
                !isMaximumFundingLevelOverridden(section);
    }

    private boolean competitionIsKtp(ApplicantSectionResource section) {
        return KTP.equals(section.getCompetition().getFundingType());
    }

    private boolean competitionIsLoanType(ApplicantSectionResource section) {
        return LOAN.equals(section.getCompetition().getFundingType());
    }

    private boolean isCompetitionOpen(ApplicantSectionResource section) {
        return !section.getCompetition().getCompetitionStatus().isLaterThan(CompetitionStatus.OPEN);
    }

    private boolean isOrganisationTypeBusiness(ApplicantSectionResource section) {
        return section.getCurrentApplicant().getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.BUSINESS.getId());
    }

    private boolean isMaximumFundingLevelOverridden(ApplicantSectionResource section) {
        return grantClaimMaximumRestService.isMaximumFundingLevelConstant(section.getCompetition().getId()).getSuccess();
    }
}