package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.MONTH;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.stream.Collectors.toList;

/**
 * Service dealing with Project finance operations
 */
@Service
public class ProjectFinanceServiceImpl extends BaseTransactionalService implements ProjectFinanceService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {

        return getProject(projectId).andOnSuccess(project ->
               projectService.getProjectUsers(projectId).andOnSuccess(projectUsers -> {
                   List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                   return generateSpendProfileForPartnerOrganisations(project, organisationIds);
               })
        );
    }

    private ServiceResult<Void> generateSpendProfileForPartnerOrganisations(Project project, List<Long> organisationIds) {

        List<ServiceResult<Void>> generationResults = simpleMap(organisationIds, organisationId -> {

            return spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisationId).
                    andOnSuccess(summaryPerCategory ->
                            generateSpendProfileForOrganisation(project.getId(), organisationId, summaryPerCategory));
        });

        return processAnyFailuresOrSucceed(generationResults);
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(
            Long projectId,
            Long organisationId,
            List<SpendProfileCostCategorySummary> summaryPerCategory) {

        return find(project(projectId), organisation(organisationId)).andOnSuccess(
                (project, organisation) -> generateSpendProfileForOrganisation(summaryPerCategory, project, organisation));
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(List<SpendProfileCostCategorySummary> summaryPerCategory, Project project, Organisation organisation) {

        return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(project.getId(), organisation.getId()).
                andOnSuccessReturnVoid(costCategoryType -> {

            List<Cost> eligibleCosts = generateEligibleCosts(summaryPerCategory, costCategoryType);
            List<Cost> spendProfileCosts = generateSpendProfileFigures(summaryPerCategory, project, costCategoryType);

            SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts);
            spendProfileRepository.save(spendProfile);
        });
    }

    private List<Cost> generateSpendProfileFigures(List<SpendProfileCostCategorySummary> summaryPerCategory, Project project, CostCategoryType costCategoryType) {

        List<List<Cost>> spendProfileCostsPerCategory = simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = findMatchingCostCategory(costCategoryType, summary);

            return IntStream.range(0, project.getDurationInMonths().intValue()).mapToObj(i -> {

                BigDecimal costValueForThisMonth = i == 0 ? summary.getFirstMonthSpend() : summary.getOtherMonthsSpend();

                return new Cost(costValueForThisMonth).
                           withCategory(matchingCategory).
                           withTimePeriod(i, MONTH, 1, MONTH);

            }).collect(toList());
        });

        return flattenLists(spendProfileCostsPerCategory);
    }

    private List<Cost> generateEligibleCosts(List<SpendProfileCostCategorySummary> summaryPerCategory, CostCategoryType costCategoryType) {

        return simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = findMatchingCostCategory(costCategoryType, summary);
            return new Cost(summary.getTotal().setScale(0, ROUND_HALF_UP)).withCategory(matchingCategory);
        });
    }

    private CostCategory findMatchingCostCategory(CostCategoryType costCategoryType, SpendProfileCostCategorySummary summary) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(summary.getCategory().getName())).get();
    }

    private Supplier<ServiceResult<Project>> project(Long id) {
        return () -> getProject(id);
    }

    private ServiceResult<Project> getProject(Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }

}
