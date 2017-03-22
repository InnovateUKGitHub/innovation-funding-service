package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service implementation for linking an {@link Application} to an {@link InnovationArea}.
 */
@Service
public class ApplicationResearchCategoryServiceImpl extends BaseTransactionalService implements ApplicationResearchCategoryService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ResearchCategoryRepository researchCategoryRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private UsersRolesService usersRolesService;

    @Override
    public ServiceResult<ApplicationResource> setResearchCategory(Long applicationId, Long researchCategoryId) {
        return find(application(applicationId)).andOnSuccess(application ->
                findResearchCategory(researchCategoryId).andOnSuccess(researchCategory ->
                        saveApplicationWithResearchCategory(application, researchCategory))).andOnSuccess(application -> serviceSuccess(applicationMapper.mapToResource(application)));
    }

    private ServiceResult<ResearchCategory> findResearchCategory(Long researchCategoryId) {
        return find(researchCategoryRepository.findById(researchCategoryId), notFoundError(ResearchCategory.class));
    }

    private ServiceResult<Application> saveApplicationWithResearchCategory(Application application, ResearchCategory researchCategory) {

        Application origApplication = applicationRepository.findOne(application.getId());

        if (origApplication.getResearchCategory() == null || !origApplication.getResearchCategory().getId().equals(researchCategory.getId())) {

            markAsIncompleteForAllCollaborators(application.getCompetition().getId(), application.getId());

            resetFundingLevels(application.getCompetition().getId(), application.getId());
        }

        application.setResearchCategory(researchCategory);

        return serviceSuccess(applicationRepository.save(application));
    }

    private void markAsIncompleteForAllCollaborators(Long competitionId, Long applicationId) {

        List<ProcessRoleResource> processRoles = usersRolesService.getAssignableProcessRolesByApplicationId(applicationId).getSuccessObjectOrThrowException();

        Set<Long> processRoleIds = processRoles.stream().filter(processRole -> processRole.getUser() != null).map(
                processRole -> processRole.getId()).collect(Collectors.toSet());

        processRoleIds.stream().forEach(processRoleId ->
                sectionService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FUNDING_FINANCES).getSuccessObjectOrThrowException().stream()
                        .forEach(fundingSection ->
                                sectionService.markSectionAsInComplete(fundingSection.getId(),
                                        applicationId, processRoleId))
        );
    }

    private void resetFundingLevels(Long competitionId, Long applicationId) {

        Question financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();

        financeRowService.financeDetails(applicationId).getSuccessObjectOrThrowException().stream().forEach(applicationFinance -> {

            if (applicationFinance.getGrantClaim() != null) {
                applicationFinance.getGrantClaim().setGrantClaimPercentage(0);
                financeRowService.addCost(applicationFinance.getId(), financeQuestion.getId(), applicationFinance.getGrantClaim());
            }
        });
    }
}
