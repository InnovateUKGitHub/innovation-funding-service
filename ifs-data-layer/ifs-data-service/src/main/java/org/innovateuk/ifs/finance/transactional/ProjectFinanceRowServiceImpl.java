package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationTypeFinanceHandler;
import org.innovateuk.ifs.finance.handler.ProjectFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service to support operations on ProjectFinanceRow.  This is only permitted for use by internal finance users.
 */
@Service
public class ProjectFinanceRowServiceImpl extends BaseTransactionalService implements ProjectFinanceRowService {
    private static final Log LOG = LogFactory.getLog(ProjectFinanceRowServiceImpl.class);

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceMapper projectFinanceMapper;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ProjectFinanceHandler projectFinanceHandler;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandler;

    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    @Override
    public ServiceResult<FinanceRowItem> get(long costItemId) {
        ProjectFinanceRow cost = projectFinanceRowRepository.findById(costItemId).get();
        ProjectFinance projectFinance = cost.getTarget();
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getProject().getApplication().getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());

        return serviceSuccess(organisationFinanceHandler.toResource(cost));
    }

    @Override
    @Transactional
    public ServiceResult<FinanceRowItem> create(FinanceRowItem newCostItem) {
        return find(projectFinance(newCostItem.getTargetId())).andOnSuccess(projectFinance -> {
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getProject().getApplication().getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());
            FinanceRow newCost = addCostItem(projectFinance, newCostItem);
            return serviceSuccess(organisationFinanceHandler.toResource(newCost));
        });
    }

    @Override
    @Transactional
    public ServiceResult<FinanceRowItem> update(final long id, final FinanceRowItem newCostItem) {
        return find(projectFinanceRowRepository.findById(id), notFoundError(ProjectFinanceRow.class)).
                andOnSuccess(projectFinanceRow -> doUpdate(id, newCostItem).andOnSuccessReturn(cost -> {
                            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(((ProjectFinanceRow) cost).getTarget().getProject().getApplication().getCompetition().getId(), ((ProjectFinanceRow) cost).getTarget().getOrganisation().getOrganisationType().getId());
                            return organisationFinanceHandler.toResource(cost);
                        })
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long costId) {
        return find(projectFinanceRowRepository.findById(costId), notFoundError(ProjectFinanceRow.class)).
                andOnSuccessReturnVoid(projectFinanceRow -> {
                    financeRowMetaValueRepository.deleteByFinanceRowId(costId);
                    projectFinanceRowRepository.deleteById(costId);
                });
    }

    @Override
    public ServiceResult<ProjectFinanceResource> financeChecksDetails(long projectId, long organisationId) {
        return getProjectFinanceForOrganisation(new ProjectFinanceResourceId(projectId, organisationId));
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(long projectId) {
        return find(projectFinanceHandler.getFinanceChecksTotals(projectId), notFoundError(ProjectFinance.class, projectId));
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowItem costItem) {
        return organisationFinanceDefaultHandler.getCostHandler(costItem.getCostType());
    }

    @Override
    public ServiceResult<Void> createProjectFinance(long projectId, long organisationId) {
        return find(project(projectId), organisation(organisationId)).andOnSuccessReturnVoid((project, organisation) -> {
            ProjectFinance projectFinance = projectFinanceRepository.save(new ProjectFinance(project, organisation));
            if (TRUE.equals(projectFinance.getCompetition().getIncludeProjectGrowthTable())) {
                projectFinance.setGrowthTable(new GrowthTable());
                growthTableRepository.save(projectFinance.getGrowthTable());
            } else {
                projectFinance.setEmployeesAndTurnover(new EmployeesAndTurnover());
                employeesAndTurnoverRepository.save(projectFinance.getEmployeesAndTurnover());
            }
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());

            for (FinanceRowType costType : projectFinance.getCompetition().getFinanceRowTypes()) {
                organisationFinanceHandler.initialiseCostType(projectFinance, costType);
            }
        });
    }

    private Supplier<ServiceResult<ProjectFinance>> projectFinance(long projectFinanceId) {
        return () -> getProjectFinance(projectFinanceId);
    }

    private ServiceResult<ProjectFinance> getProjectFinance(long projectFinanceId) {
        return find(projectFinanceRepository.findById(projectFinanceId), notFoundError(ProjectFinance.class, projectFinanceId));
    }

    private ServiceResult<ProjectFinanceResource> getProjectFinanceForOrganisation(ProjectFinanceResourceId projectFinanceResourceId) {
        return projectFinanceHandler.getProjectOrganisationFinances(projectFinanceResourceId);
    }

    private FinanceRow addCostItem(ProjectFinance projectFinance, FinanceRowItem newCostItem) {
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getProject().getApplication().getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());

        ProjectFinanceRow cost = organisationFinanceHandler.toProjectDomain(newCostItem);
        cost.setType(newCostItem.getCostType());
        cost.setTarget(projectFinance);

        return persistCostHandlingCostValues(cost);
    }

    private ProjectFinanceRow persistCostHandlingCostValues(ProjectFinanceRow cost) {

        List<FinanceRowMetaValue> costValues = cost.getFinanceRowMetadata();
        cost.setFinanceRowMetadata(new ArrayList<>());
        ProjectFinanceRow persistedCost = projectFinanceRowRepository.save(cost);
        costValues.forEach(costVal -> costVal.setFinanceRowId(persistedCost.getId()));
        persistedCost.setFinanceRowMetadata(costValues);
        financeRowMetaValueRepository.saveAll(costValues);
        return projectFinanceRowRepository.save(persistedCost);
    }

    private Supplier<ServiceResult<ProjectFinanceRow>> cost(long costId) {
        return () -> getCost(costId);
    }

    private ServiceResult<ProjectFinanceRow> getCost(long costId) {
        return find(projectFinanceRowRepository.findById(costId), notFoundError(ProjectFinanceRow.class));
    }

    private ProjectFinanceRow mapCost(ProjectFinanceRow currentCost, ProjectFinanceRow newCost) {
        if (newCost.getCost() != null) {
            currentCost.setCost(newCost.getCost());
        }
        if (newCost.getDescription() != null) {
            currentCost.setDescription(newCost.getDescription());
        }
        if (newCost.getItem() != null) {
            currentCost.setItem(newCost.getItem());
        }
        if (newCost.getQuantity() != null) {
            currentCost.setQuantity(newCost.getQuantity());
        }
        if (newCost.getApplicationRowId() != null) {
            currentCost.setApplicationRowId(newCost.getApplicationRowId());
        }

        return currentCost;
    }

    private void updateCostValue(FinanceRowMetaValue costValue, FinanceRow savedCost) {
        if (costValue.getFinanceRowMetaField() == null) {
            LOG.error("FinanceRowMetaField is null");
            return;
        }
        FinanceRowMetaField financeRowMetaField = financeRowMetaFieldRepository.findById(costValue.getFinanceRowMetaField().getId()).orElse(null);
        costValue.setFinanceRowId(savedCost.getId());
        costValue.setFinanceRowMetaField(financeRowMetaField);
        costValue = financeRowMetaValueRepository.save(costValue);
        savedCost.addCostValues(costValue);
    }

    private ServiceResult<FinanceRow> doUpdate(Long id, FinanceRowItem newCostItem) {
        return find(cost(id)).andOnSuccessReturn(existingCost -> {
            ProjectFinance projectFinance = existingCost.getTarget();
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getProject().getApplication().getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());
            ProjectFinanceRow newCost = organisationFinanceHandler.toProjectDomain(newCostItem);
            ProjectFinanceRow updatedCost = mapCost(existingCost, newCost);

            ProjectFinanceRow savedCost = projectFinanceRowRepository.save(updatedCost);

            newCost.getFinanceRowMetadata()
                    .stream()
                    .filter(c -> c.getValue() != null)
                    .filter(c -> !"null".equals(c.getValue()))
                    .peek(c -> LOG.debug("FinanceRowMetaValue: " + c.getValue()))
                    .forEach(costValue -> updateCostValue(costValue, savedCost));

            // refresh the object, since we need to reload the costvalues, on the cost object.
            return savedCost;
        });
    }
}
