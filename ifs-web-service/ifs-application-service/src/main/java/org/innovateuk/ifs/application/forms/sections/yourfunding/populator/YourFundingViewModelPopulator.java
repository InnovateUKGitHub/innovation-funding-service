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
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

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
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Value("${ifs.funding.level.decimal.percentage.enabled}")
    private boolean fundingLevelPercentageToggle;

    public YourFundingViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        if (user.isInternalUser()) {
            return populateManagement(applicationId, sectionId, organisationId);
        }
        return populate(applicationId, sectionId, user);
    }

    private YourFundingViewModel populate(long applicationId, long sectionId, UserResource user) {

        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section
                .getCurrentApplicant().getOrganisation().getId());
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getApplicationFinance(applicationId, section.getCurrentApplicant().getOrganisation().getId()).getSuccess();

        boolean complete = section.isComplete(section.getCurrentApplicant());
        boolean open = section.getApplication().isOpen() &&
                section.getCompetition().isOpen();

        Long researchCategoryQuestionId = getResearchCategoryQuestionId(section);
        boolean researchCategoryRequired = isResearchCategoryRequired(section, researchCategoryQuestionId);
        long yourOrganisationSectionId = getYourOrganisationSectionId(section);
        boolean yourOrganisationRequired = !completedSectionIds.contains(yourOrganisationSectionId);
        boolean fundingSectionLocked = isFundingSectionLocked(section, researchCategoryRequired,
                yourOrganisationRequired);
        boolean overridingFundingRules = isMaximumFundingLevelOverridden(section);

        return new YourFundingViewModel(applicationId,
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
                researchCategoryQuestionId,
                yourOrganisationSectionId,
                applicationFinance.getMaximumFundingLevel(),
                format("/application/%d/form/FINANCE", applicationId),
                overridingFundingRules,
                fundingLevelPercentageToggle);
    }

    private ManagementYourFundingViewModel populateManagement(long applicationId, long sectionId, long organisationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        return new ManagementYourFundingViewModel(applicationId, sectionId, organisationId, application.getCompetition(), application.getName(),
                format("/application/%d/form/FINANCE/%d", applicationId, organisationId), fundingLevelPercentageToggle);

    }

    private Long getResearchCategoryQuestionId(ApplicantSectionResource section) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(),
                RESEARCH_CATEGORY).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private boolean isResearchCategoryRequired(ApplicantSectionResource section, Long researchCategoryQuestionId) {
        return researchCategoryQuestionId != null && !isResearchCategoryComplete(section, researchCategoryQuestionId);
    }

    private boolean isResearchCategoryComplete(ApplicantSectionResource section, long questionId) {
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
                                           boolean yourOrganisationRequired) {
        boolean fieldsRequired = researchCategoryRequired || yourOrganisationRequired;
        return fieldsRequired && isCompetitionOpen(section) && isOrganisationTypeBusiness(section) &&
                !isMaximumFundingLevelOverridden(section) && !competitionIsLoanType(section);
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
        return grantClaimMaximumRestService.isMaximumFundingLevelOverridden(section.getCompetition().getId()).getSuccess();
    }
}