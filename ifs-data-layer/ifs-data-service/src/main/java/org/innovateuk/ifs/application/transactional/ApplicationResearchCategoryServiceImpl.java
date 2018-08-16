package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service implementation for linking an {@link Application} to an {@link InnovationArea}.
 */
@Service
public class ApplicationResearchCategoryServiceImpl extends BaseTransactionalService implements ApplicationResearchCategoryService {

    @Autowired
    private ResearchCategoryRepository researchCategoryRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionStatusService sectionStatusService;

    @Autowired
    private FinanceRowCostsService financeRowCostsService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UsersRolesService usersRolesService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private GrantClaimMaximumService grantClaimMaximumService;

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> setResearchCategory(Long applicationId, Long researchCategoryId) {
        return find(application(applicationId)).andOnSuccess(application ->
                findResearchCategory(researchCategoryId).andOnSuccess(researchCategory ->
                        saveApplicationWithResearchCategory(application, researchCategory))).andOnSuccess(application -> serviceSuccess(applicationMapper.mapToResource(application)));
    }

    private ServiceResult<ResearchCategory> findResearchCategory(Long researchCategoryId) {
        return find(researchCategoryRepository.findOne(researchCategoryId), notFoundError(ResearchCategory.class));
    }

    private ServiceResult<Application> saveApplicationWithResearchCategory(Application application, ResearchCategory researchCategory) {

        Application origApplication = applicationRepository.findOne(application.getId());

        if (origApplication.getResearchCategory() == null || !origApplication.getResearchCategory().getId().equals(researchCategory.getId())) {

            if (canResetFundingLevels(application)) {
                markAsIncompleteForAllCollaborators(application.getCompetition().getId(), application.getId());
                resetFundingLevels(application.getCompetition().getId(), application.getId());
            }
        }

        application.setResearchCategory(researchCategory);

        return serviceSuccess(applicationRepository.save(application));
    }

    private void markAsIncompleteForAllCollaborators(Long competitionId, Long applicationId) {

        List<ProcessRoleResource> processRoles = usersRolesService.getAssignableProcessRolesByApplicationId(applicationId).getSuccess();

        Set<Long> processRoleIds = processRoles.stream().filter(processRole -> processRole.getUser() != null).map(
                processRole -> processRole.getId()).collect(Collectors.toSet());

        processRoleIds.stream().forEach(processRoleId ->
                sectionService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FUNDING_FINANCES).getSuccess().stream()
                        .forEach(fundingSection ->
                                sectionStatusService.markSectionAsInComplete(fundingSection.getId(),
                                        applicationId, processRoleId))
        );
    }

    private void resetFundingLevels(Long competitionId, Long applicationId) {
        Optional<Question> financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getOptionalSuccessObject();

        financeService.financeDetails(applicationId)
                .getOptionalSuccessObject()
                .ifPresent(applicationFinanceResources ->
                        applicationFinanceResources.forEach(applicationFinance -> {
                            if (applicationFinance.getGrantClaim() != null && financeQuestion.isPresent()) {
                                applicationFinance.getGrantClaim().setGrantClaimPercentage(0);
                                financeRowCostsService.addCost(applicationFinance.getId(), financeQuestion.get().getId(), applicationFinance.getGrantClaim());
                            }
                        })
                );
    }

    private boolean canResetFundingLevels(Application application) {
        OrganisationResource organisation = organisationService.findById(application.getLeadOrganisationId()).getSuccess();
        Set<Long> templateGcms = grantClaimMaximumService.getGrantClaimMaximumsForCompetitionType(organisation.getOrganisationType()).getSuccess();
        Set<Long> competitionGcms = grantClaimMaximumService.getGrantClaimMaximumsForCompetition(application.getCompetition().getId()).getSuccess();

        return organisation.getOrganisationType().equals(BUSINESS.getId()) && templateGcms.equals(competitionGcms);
    }
}
