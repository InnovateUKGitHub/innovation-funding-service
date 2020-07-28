package org.innovateuk.ifs.project.financechecks.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.financechecks.domain.*;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * A Component separate from the main ProjectService for specifically generating Finances for Projects
 */
@Component
public class FinanceChecksGenerator {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    public ServiceResult<Void> createMvpFinanceChecksFigures(Project newProject, Organisation organisation, CostCategoryType costCategoryType) {
        FinanceCheck newFinanceCheck = createMvpFinanceCheckEmptyCosts(newProject, organisation, costCategoryType);
        populateFinanceCheck(newFinanceCheck);
        return serviceSuccess();
    }

    public ServiceResult<ProjectFinance> createFinanceChecksFigures(Project newProject, Organisation organisation) {
        return copyFinanceChecksFromApplicationFinances(newProject, organisation);
    }

    private ServiceResult<ProjectFinance> copyFinanceChecksFromApplicationFinances(Project newProject, Organisation organisation) {
        ApplicationFinance applicationFinanceForOrganisation =
                applicationFinanceRepository.findByApplicationIdAndOrganisationId(newProject.getApplication().getId(), organisation.getId()).get();

        EmployeesAndTurnover employeesAndTurnover = applicationFinanceForOrganisation.getEmployeesAndTurnover();
        if (employeesAndTurnover != null) {
            employeesAndTurnover = employeesAndTurnoverRepository.save(new EmployeesAndTurnover(employeesAndTurnover));
        }
        GrowthTable growthTable = applicationFinanceForOrganisation.getGrowthTable();
        if (growthTable != null) {
            growthTable = growthTableRepository.save(new GrowthTable(growthTable));
        }
        ProjectFinance projectFinance = new ProjectFinance(organisation, applicationFinanceForOrganisation.getOrganisationSize(), newProject, growthTable, employeesAndTurnover);

        CompetitionResource competition = competitionService.getCompetitionById(applicationFinanceForOrganisation.getApplication().getCompetition().getId()).getSuccess();

        if(competition.applicantNotRequiredForViabilityChecks(organisation.getOrganisationTypeEnum())) {
            PartnerOrganisation partnerOrganisation = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(newProject.getId(), organisation.getId());
            viabilityWorkflowHandler.viabilityNotApplicable(partnerOrganisation, null);
        }

        ProjectFinance projectFinanceForOrganisation =
                projectFinanceRepository.save(projectFinance);

        List<ApplicationFinanceRow> originalFinanceFigures = applicationFinanceRowRepository.findByTargetId(applicationFinanceForOrganisation.getId());

        List<ProjectFinanceRow> copiedFinanceFigures = simpleMap(originalFinanceFigures, original -> {
            ProjectFinanceRow newRow = new ProjectFinanceRow(projectFinanceForOrganisation);
            newRow.setApplicationRowId(original.getId());
            newRow.setCost(original.getCost());
            List<FinanceRowMetaValue> metaValues = simpleMap(original.getFinanceRowMetadata(), costValue -> copyFinanceRowMetaValue(newRow, costValue));
            newRow.setFinanceRowMetadata(metaValues);
            newRow.setDescription(original.getDescription());
            // map H2020 totals directly to conventional totals as they are treated exactly the same in project setup
            newRow.setItem("HORIZON_2020_TOTAL".equals(original.getItem()) ? "TOTAL" : original.getItem());
            newRow.setName(original.getName());
            newRow.setQuantity(original.getQuantity());
            newRow.setType(original.getType());
            return newRow;
        });

        copiedFinanceFigures.forEach(figure -> {

            ProjectFinanceRow savedFigure = projectFinanceRowRepository.save(figure);

            figure.getFinanceRowMetadata().forEach(metaValue -> {
                metaValue.setFinanceRowId(savedFigure.getId());
                financeRowMetaValueRepository.save(metaValue);
            });
        });
        return serviceSuccess(projectFinance);
    }

    private FinanceCheck createMvpFinanceCheckEmptyCosts(Project newProject, Organisation organisation, CostCategoryType costCategoryType) {

        List<Cost> costs = new ArrayList<>();
        List<CostCategory> costCategories = costCategoryType.getCostCategories();
        CostGroup costGroup = new CostGroup("finance-check", costs);
        costCategories.forEach(costCategory -> {
            Cost cost = new Cost(BigDecimal.valueOf(0.0));
            costs.add(cost);
            cost.setCostCategory(costCategory);
        });
        costGroup.setCosts(costs);

        FinanceCheck financeCheck = new FinanceCheck(newProject, costGroup);
        financeCheck.setOrganisation(organisation);
        financeCheck.setProject(newProject);

        return financeCheck;
    }

    private FinanceCheck populateFinanceCheck(FinanceCheck financeCheck) {
        Organisation organisation = financeCheck.getOrganisation();
        Application application = financeCheck.getProject().getApplication();
        if (OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId())) {
            Optional<ApplicationFinance> applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId());
            List<ApplicationFinanceRow> financeRows = financeRowRepository.findByTargetId(applicationFinance.get().getId());
            financeCheck.getCostGroup().getCosts().forEach(
                    c -> c.setValue(AcademicCostCategoryGenerator.findCost(c.getCostCategory(), financeRows))
            );
        }
        return financeCheckRepository.save(financeCheck);
    }

    private FinanceRowMetaValue copyFinanceRowMetaValue(ProjectFinanceRow row, FinanceRowMetaValue costValue) {
        return new FinanceRowMetaValue(row, costValue.getFinanceRowMetaField(), costValue.getValue());
    }
}
