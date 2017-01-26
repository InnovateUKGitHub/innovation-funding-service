package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
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

    @Override
    public ServiceResult<List<? extends FinanceRow>> getCosts(Long projectFinanceId, String costTypeName, Long questionId) {
        // TODO: 4834
        return null;
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, String costTypeName, Long questionId) {
        // TODO: 4834
        return null;
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, Long questionId) {
        return null;
    }

    @Override
    public ServiceResult<FinanceRowItem> addCost(Long projectFinanceId, Long questionId, FinanceRowItem newCostItem) {
        return find(question(questionId), projectFinance(projectFinanceId)).andOnSuccess((question, projectFinance) -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getName());
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
    public ServiceResult<FinanceRowItem> addCostWithoutPersisting(final Long projectFinanceId, final Long questionId) {
        return find(question(questionId), projectFinance(projectFinanceId)).andOnSuccess((question, projectFinance) ->
                getProject(projectFinance.getProject().getId()).andOnSuccess(project -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getName());
                    ProjectFinanceRow cost = new ProjectFinanceRow(projectFinance, question);
                    return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
                })
        );
    }
    @Override
    public ServiceResult<Void> deleteCost(@P("costId") Long costId) {
        return null;
    }

    @Override
    public ServiceResult<ProjectFinanceResource> updateCost(Long projectFinanceId, ProjectFinanceResource projectFinance) {
        return getProject(projectFinance.getProject()).andOnSuccess(project ->
                find(projectFinance(projectFinanceId)).andOnSuccess(dbFinance -> {
                    dbFinance.setOrganisationSize(projectFinance.getOrganisationSize());
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
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getOrganisation().getOrganisationType().getName());

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
}
