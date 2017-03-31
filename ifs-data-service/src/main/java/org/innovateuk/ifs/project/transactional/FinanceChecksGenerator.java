package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.domain.*;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.finance.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private FinanceUtil financeUtil;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    public ServiceResult<Void> createMvpFinanceChecksFigures(Project newProject, Organisation organisation, CostCategoryType costCategoryType) {
        FinanceCheck newFinanceCheck = createMvpFinanceCheckEmptyCosts(newProject, organisation, costCategoryType);
        populateFinanceCheck(newFinanceCheck);
        return serviceSuccess();
    }

    public ServiceResult<Void> createFinanceChecksFigures(Project newProject, Organisation organisation) {
        copyFinanceChecksFromApplicationFinances(newProject, organisation);
        return serviceSuccess();
    }

    private void copyFinanceChecksFromApplicationFinances(Project newProject, Organisation organisation) {

        ApplicationFinance applicationFinanceForOrganisation =
                applicationFinanceRepository.findByApplicationIdAndOrganisationId(newProject.getApplication().getId(), organisation.getId());

        ProjectFinance projectFinance = new ProjectFinance(organisation, applicationFinanceForOrganisation.getOrganisationSize(), newProject);

        if (financeUtil.isUsingJesFinances(organisation.getOrganisationType().getId())) {

            PartnerOrganisation partnerOrganisation = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(newProject.getId(), organisation.getId());
            viabilityWorkflowHandler.organisationIsAcademic(partnerOrganisation, null);
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
            newRow.setItem(original.getItem());
            newRow.setName(original.getName());
            newRow.setQuantity(original.getQuantity());
            newRow.setQuestion(original.getQuestion());
            return newRow;
        });

        copiedFinanceFigures.forEach(figure -> {

            ProjectFinanceRow savedFigure = projectFinanceRowRepository.save(figure);

            figure.getFinanceRowMetadata().forEach(metaValue -> {
                metaValue.setFinanceRowId(savedFigure.getId());
                financeRowMetaValueRepository.save(metaValue);
            });
        });
    }

    private FinanceCheck createMvpFinanceCheckEmptyCosts(Project newProject, Organisation organisation, CostCategoryType costCategoryType) {

        List<Cost> costs = new ArrayList<>();
        List<CostCategory> costCategories = costCategoryType.getCostCategories();
        CostGroup costGroup = new CostGroup("finance-check", costs);
        costCategories.forEach(costCategory -> {
            Cost cost = new Cost(new BigDecimal(0.0));
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
        if (financeUtil.isUsingJesFinances(organisation.getOrganisationType().getName())) {
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId());
            List<ApplicationFinanceRow> financeRows = financeRowRepository.findByTargetId(applicationFinance.getId());
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
