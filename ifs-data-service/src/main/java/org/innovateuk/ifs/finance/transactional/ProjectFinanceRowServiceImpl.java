package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDefaultHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceRowMapper;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private ProjectFinanceRowMapper projectFinanceRowMapper;

    @Autowired
    OrganisationFinanceDefaultHandler organisationFinanceDefaultHandler;

    @Autowired
    private OrganisationSizeRepository organisationSizeRepository;

    @Override
    public ServiceResult<List<? extends FinanceRow>> getCosts(Long projectFinanceId, String costTypeName, Long questionId) {
        throw new NotImplementedException("This method enforced by interface is not required for Project finance");
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, String costTypeName, Long questionId) {
        throw new NotImplementedException("This method enforced by interface is not required for Project finance");
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, Long questionId) {
        throw new NotImplementedException("This method enforced by interface is not required for Project finance");
    }

    @Override
    public ServiceResult<FinanceRowItem> getCostItem(Long costItemId) {
        ProjectFinanceRow cost = projectFinanceRowRepository.findOne(costItemId);
        ProjectFinance projectFinance = cost.getTarget();
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getId());

        return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
    }

    @Override
    public ServiceResult<FinanceRowItem> addCost(Long projectFinanceId, Long questionId, FinanceRowItem newCostItem) {
        return find(question(questionId), projectFinance(projectFinanceId)).andOnSuccess((question, projectFinance) -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getId());
                    if (newCostItem != null) {
                        FinanceRow newCost = addCostItem(projectFinance, question, newCostItem);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem((ProjectFinanceRow) newCost));
                    } else {
                        ProjectFinanceRow cost = new ProjectFinanceRow(projectFinance, question);
                        projectFinanceRowRepository.save(cost);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
                    }
                }
        );
    }

    @Override
    public ServiceResult<FinanceRowItem> updateCost(final Long id, final FinanceRowItem newCostItem) {

        return find(projectFinanceRowRepository.findOne(id), notFoundError(ProjectFinanceRow.class)).
                andOnSuccess(projectFinanceRow -> doUpdate(id, newCostItem).andOnSuccessReturn(cost -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(((ProjectFinanceRow)cost).getTarget().getOrganisation().getOrganisationType().getId());
                    return organisationFinanceHandler.costToCostItem((ProjectFinanceRow)cost);
                })
        );
    }

    @Override
    public ServiceResult<FinanceRowItem> addCostWithoutPersisting(final Long projectFinanceId, final Long questionId) {
        return find(question(questionId), projectFinance(projectFinanceId)).andOnSuccess((question, projectFinance) ->
                getProject(projectFinance.getProject().getId()).andOnSuccess(project -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getId());
                    ProjectFinanceRow cost = new ProjectFinanceRow(projectFinance, question);
                    return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
                })
        );
    }

    @Override
    public ServiceResult<Void> deleteCost(@P("costId") Long costId) {
        return find(projectFinanceRowRepository.findOne(costId), notFoundError(ProjectFinanceRow.class)).
                andOnSuccess(projectFinanceRow ->{
                    financeRowMetaValueRepository.deleteByFinanceRowId(costId);
                    projectFinanceRowRepository.delete(costId);
                    return serviceSuccess();
                });
    }

    @Override
    public ServiceResult<ProjectFinanceResource> updateCost(Long projectFinanceId, ProjectFinanceResource projectFinance) {
        return getProject(projectFinance.getProject()).andOnSuccess(project ->
                find(projectFinance(projectFinanceId)).andOnSuccess(dbFinance -> {
                    if (projectFinance.getOrganisationSize() != null) {
                        dbFinance.setOrganisationSize(organisationSizeRepository.findOne(projectFinance.getOrganisationSize()));
                    }
                    dbFinance = projectFinanceRepository.save(dbFinance);
                    return serviceSuccess(projectFinanceMapper.mapToResource(dbFinance));
                })
        );
    }

    @Override
    public ServiceResult<ProjectFinanceResource> financeChecksDetails(Long projectId, Long organisationId) {
        ProjectFinanceResourceId projectFinanceResourceId = new ProjectFinanceResourceId(projectId, organisationId);
        return getProjectFinanceForOrganisation(projectFinanceResourceId);
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(Long projectId) {
        return find(applicationFinanceHandler.getFinanceChecksTotals(projectId), notFoundError(ProjectFinance.class, projectId));
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowItem costItem) {
        return organisationFinanceDefaultHandler.getCostHandler(costItem.getCostType());
    }

    private Supplier<ServiceResult<ProjectFinance>> projectFinance(Long projectFinanceId) {
        return () -> getProjectFinance(projectFinanceId);
    }

    private ServiceResult<ProjectFinance> getProjectFinance(Long projectFinanceId) {
        return find(projectFinanceRepository.findOne(projectFinanceId), notFoundError(ProjectFinance.class, projectFinanceId));
    }

    private ServiceResult<ProjectFinanceResource> getProjectFinanceForOrganisation(ProjectFinanceResourceId projectFinanceResourceId) {
        return serviceSuccess(applicationFinanceHandler.getProjectOrganisationFinances(projectFinanceResourceId));
    }

    private FinanceRow addCostItem(ProjectFinance projectFinance, Question question, FinanceRowItem newCostItem) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getId());

        ProjectFinanceRow cost = organisationFinanceHandler.costItemToProjectCost(newCostItem);
        cost.setQuestion(question);
        cost.setTarget(projectFinance);

        return persistCostHandlingCostValues(cost);
    }

    private ProjectFinanceRow persistCostHandlingCostValues(ProjectFinanceRow cost) {

        List<FinanceRowMetaValue> costValues = cost.getFinanceRowMetadata();
        cost.setFinanceRowMetadata(new ArrayList<>());
        ProjectFinanceRow persistedCost = projectFinanceRowRepository.save(cost);
        costValues.stream().forEach(costVal -> costVal.setFinanceRowId(persistedCost.getId()));
        persistedCost.setFinanceRowMetadata(costValues);
        financeRowMetaValueRepository.save(costValues);
        return projectFinanceRowRepository.save(persistedCost);
    }

    private Supplier<ServiceResult<ProjectFinanceRow>> cost(Long costId) {
        return () -> getCost(costId);
    }

    private ServiceResult<ProjectFinanceRow> getCost(Long costId) {
        return find(projectFinanceRowRepository.findOne(costId), notFoundError(ProjectFinanceRow.class));
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
        if(newCost.getApplicationRowId() != null) {
            currentCost.setApplicationRowId(newCost.getApplicationRowId());
        }

        return currentCost;
    }

    private void updateCostValue(FinanceRowMetaValue costValue, FinanceRow savedCost) {
        if (costValue.getFinanceRowMetaField() == null) {
            LOG.error("FinanceRowMetaField is null");
            return;
        }
        FinanceRowMetaField financeRowMetaField = financeRowMetaFieldRepository.findOne(costValue.getFinanceRowMetaField().getId());
        costValue.setFinanceRowId(savedCost.getId());
        costValue.setFinanceRowMetaField(financeRowMetaField);
        costValue = financeRowMetaValueRepository.save(costValue);
        savedCost.addCostValues(costValue);
    }

    private ServiceResult<FinanceRow> doUpdate(Long id, FinanceRowItem newCostItem) {
        return find(cost(id)).andOnSuccessReturn(existingCost -> {
            ProjectFinance projectFinance = existingCost.getTarget();
            OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getId());
            ProjectFinanceRow newCost = organisationFinanceHandler.costItemToProjectCost(newCostItem);
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
